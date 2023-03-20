package com.pcs.daejeon.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.requests.IGRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadPhotoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import com.github.instagram4j.instagram4j.responses.media.RuploadPhotoResponse;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Like;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.LikeRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final Util util;
    //    private final IGClient client;

    public Long writePost(String description) throws MethodArgumentNotValidException {
        description = description.replace("\n", " ");

//        ------------- 욕설 필터링 임시 해지 --------------
//        boolean isBad = isBadDesc(description);
//        if (isBad) {
//            throw new IllegalArgumentException("bad words");
//        }

        Member loginMember = util.getLoginMember();
        Post save = postRepository.save(new Post(description, loginMember.getSchool()));
        return save.getId();
    }

    /**
     * check is description has bad words
     * @param description
     * @return
     */
    private static boolean isBadDesc(String description) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://spocjaxkrk.execute-api.ap-northeast-2.amazonaws.com/v1/detect?comment=" + description).build();

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            if (
                resultMap.hasBody() && (
                resultMap.getBody().get("abuse").equals(1) ||
                resultMap.getBody().get("age").equals(1) ||
                resultMap.getBody().get("binan").equals(1) ||
                resultMap.getBody().get("gender").equals(1) ||
                resultMap.getBody().get("hansome").equals(1) ||
                resultMap.getBody().get("harassment").equals(1) ||
                resultMap.getBody().get("native").equals(1) ||
                resultMap.getBody().get("poli").equals(1) ||
                resultMap.getBody().get("race").equals(1) ||
                resultMap.getBody().get("religion").equals(1))
            ) {
                return true;
            }

            return false;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException("description valid api server error");
        }
    }


    public void deletePost(Long postId) {
        Post post = findPostById(postId);

        if (!Objects.equals(util.getLoginMember().getSchool().getId(), post.getSchool().getId())) {
            throw new IllegalStateException("school is different");
        }

        post.setPostType(PostType.DELETE);
        log.info("[delete-post] delete post: id["+ post.getId() +"]"+ util.getLoginMember().getId());
    }

    public void acceptPost(Long postId) {
        Post post = findPostById(postId);

        if (!Objects.equals(util.getLoginMember().getSchool().getId(), post.getSchool().getId())) {
            throw new IllegalStateException("school is different");
        }

        post.setPostType(PostType.ACCEPTED);

        Member loginMember = util.getLoginMember();
        log.info("[accept-post] accept post: id["+ post.getId() +"] by - "+ loginMember.getName()+"["+ loginMember.getId()+"] --- ");
    }

    public Page<Tuple> findPagedPost(Pageable page) {
        return postRepository.pagingPost(page);
    }

    public Page<Post> findPagedPostByMemberId(Pageable pageable) {
        Member loginMember = util.getLoginMember();
        return postRepository.pagingPostByMemberId(pageable, loginMember);
    }
    /**
        memberId와 reportCount는 검색을 위한 인자. nullable함
     */
    public Page<Post> findPagedRejectedPost(Pageable page, Long memberId, Long reportCount) {
        return postRepository.pagingRejectPost(page, memberId, reportCount);
    }

    /**
     미신고 게시글도 함께 검색함.
     */
    // TODO 게시글 아이디 만으로도 검색 가능토록 업데이트
    public Page<Post> searchPost(Pageable pageable, Long memberId, Long reportCount) {
        return postRepository.searchPost(pageable,
                memberId,
                reportCount,
                util.getLoginMember().getSchool());
    }

    public Post findPostById(Long postId) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isEmpty()) {
            throw new IllegalStateException("not found post");
        }
        return findPost.get();
    }


    public void addLike(Long postId) throws IOException, URISyntaxException {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalStateException("post not found");
        }
        Member loginMember = util.getLoginMember();
        if (!Objects.equals(post.get().getSchool().getId(), loginMember.getSchool().getId())) {
            throw new IllegalStateException("school is different");
        }

        if (likeRepository.validLike(loginMember, postId)) {
            Like like = likeRepository.findByPostAndLikedBy(post.get(), loginMember);
            likeRepository.delete(like);
            return;
        }

        Like like = new Like(util.getLoginMember(), post.get());
        likeRepository.save(like);

        Long likedCount = likeRepository.countByPost(post.get());
        if (likedCount == 15) {
            imageCaption(post.get().getDescription());
            School school = loginMember.getSchool();
            convertPngToJpg();
            uploadToInstagram(school.getInstaId(), school.getInstaPwd());
        }
    }

    private IGClient igClient(String instaId, String instaPwd) throws IGLoginException {
    IGClient client = IGClient.builder()
                .username(instaId)
                .password(instaPwd)
                .login();

        return client;
    }

    private void uploadToInstagram(String instaId, String instaPwd) throws IOException {
        IGClient client = igClient(instaId, instaPwd);
        File file = new File(System.getProperty("user.dir")+"/src/textImage.jpg");
        byte[] imgData = Files.readAllBytes(file.toPath());
        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
        String id = client.sendRequest(uploadReq).join().getUpload_id();
        IGRequest<MediaResponse.MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
                new MediaConfigureTimelineRequest.MediaConfigurePayload().upload_id(id).caption(""));
        MediaResponse.MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();
    }

    private void convertPngToJpg() {
        try {
            File inputFile = new File(System.getProperty("user.dir")+"/src/textImage.png");
            BufferedImage inputImage = ImageIO.read(inputFile);

            BufferedImage result = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(inputImage, 0, 0, Color.WHITE, null);
            File outputFile = new File(System.getProperty("user.dir")+"/src/textImage.jpg");

            ImageIO.write(result, "jpg", outputFile);
        } catch (IOException ex) {
            System.err.println("Error converting PNG to JPEG: " + ex.getMessage());
        }
    }

    private void imageCaption(String description) {
        try {
            String imagePath = System.getProperty("user.dir") + "/src/template.png";

            BufferedImage image = ImageIO.read(new File(imagePath));

            // create a graphics context for the image
            Graphics2D g2d = image.createGraphics();

            // set the font and color for the caption
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "/src/NotoSansKR-Bold.otf")).deriveFont(Font.BOLD, 50f);
            Color color = Color.BLACK;

            // get the dimensions of the image and caption text
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int captionMaxWidth = imageWidth - 400;
            int captionHeight = g2d.getFontMetrics(font).getHeight();

            String[] lines = splitTextIntoLines(description, font, captionMaxWidth);

            // calculate the position to draw the caption in the center of the image
            int x = (imageWidth - captionMaxWidth) / 2;
            int y = (imageHeight + captionHeight) / 2;

            // draw the caption on the image
            g2d.setFont(font);
            g2d.setColor(color);

            g2d.setFont(font);
            g2d.setColor(color);
            for (int i = 0; i < lines.length; i++) {
                g2d.drawString(lines[i], x, y + (i * captionHeight));
            }

            // dispose of the graphics context
            g2d.dispose();

            // save the image with the caption
            String newImagePath = System.getProperty("user.dir") + "/src/textImage.png";
            ImageIO.write(image, "png", new File(newImagePath));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String[] splitTextIntoLines(String text, Font font, int maxWidth) {
        String[] words = text.split("");
        StringBuilder currentLine = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<String>();

        for (String word : words) {
            if (font.getStringBounds(currentLine + word, new java.awt.font.FontRenderContext(null, true, true)).getWidth() <= maxWidth) {
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word);
            }
        }
        lines.add(currentLine.toString().trim());

        return lines.toArray(new String[lines.size()]);
    }
}
