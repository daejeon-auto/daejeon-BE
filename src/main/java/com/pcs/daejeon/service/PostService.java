package com.pcs.daejeon.service;

import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.QueryResults;
import gui.ava.html.image.generator.HtmlImageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
//    private final IGClient client;

    public Long writePost(String description) {
        boolean isOk = isBadDesc(description);
        if (!isOk) {
            throw new IllegalArgumentException("bad words");
        }

        Post save = postRepository.save(new Post(description));
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
                !resultMap.hasBody() ||
                resultMap.getBody().get("abuse") == "1" ||
                resultMap.getBody().get("age") == "1" ||
                resultMap.getBody().get("binan") == "1" ||
                resultMap.getBody().get("gender") == "1" ||
                resultMap.getBody().get("hansome") == "1" ||
                resultMap.getBody().get("harassment") == "1" ||
                resultMap.getBody().get("native") == "1" ||
                resultMap.getBody().get("poli") == "1" ||
                resultMap.getBody().get("race") == "1" ||
                resultMap.getBody().get("religion") == "1"
            ) {
                return false;
            }

            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException("description valid api server error");
        }
    }


    public void rejectPost(Long postId) {
        Post post = findPostById(postId);

        post.setPostType(PostType.REJECTED);
    }

    public void acceptPost(Long postId) {
        Post post = findPostById(postId);

        post.setPostType(PostType.ACCEPTED);
    }

    public QueryResults<Post> findPagedPost(Pageable page) {
        return postRepository.pagingPost(page);
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
            throw new IllegalArgumentException("post not found");
        }

        int likedCount = post.get().addLiked();

        if (likedCount == 15) {
            drawImage(post.get().getDescription());
        }
    }

    private void drawImage(String description) throws IOException, URISyntaxException {
        StringBuilder text = new StringBuilder(description);
        int iterCount = 0;
        for (int i = 1; i <= description.length(); i++) {
            if ((i % 20) == 0) {
                text.insert(i + 6 * iterCount, "<br />");
                iterCount++;
            }
        }

        String code = "<div style=\"font-family: Malgun Gothic; font-size: 70px;\">"+text.toString()+"</div>";

        try {
            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
            imageGenerator.loadHtml(code);
            imageGenerator.saveAsImage(System.getProperty("user.dir")+"/src/textImage.png");
            convertPngToJpg();
//            uploadToInstagram();
        } catch (IllegalArgumentException e) {
            System.out.println("e = " + e);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void uploadToInstagram() throws IOException {
//        File file = new File(System.getProperty("user.dir")+"/src/textImage.jpg");
//        byte[] imgData = Files.readAllBytes(file.toPath());
//        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
//        String id = client.sendRequest(uploadReq).join().getUpload_id();
//        IGRequest<MediaResponse.MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
//                new MediaConfigureTimelineRequest.MediaConfigurePayload().upload_id(id).caption("👍👍"));
//        MediaResponse.MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();
//    }

    private void convertPngToJpg() throws IOException {
        Path source = Paths.get(System.getProperty("user.dir")+"/src/textImage.png");
        Path target = Paths.get(System.getProperty("user.dir")+"/src/textImage.jpg");

        BufferedImage originalImage = ImageIO.read(source.toFile());

        // create a blank, RGB, same width and height
        BufferedImage newBufferedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // draw a white background and puts the originalImage on it.
        newBufferedImage.createGraphics()
                .drawImage(originalImage,
                        0,
                        0,
                        Color.WHITE,
                        null);

        // save an image
        ImageIO.write(newBufferedImage, "jpg", target.toFile());
    }
}
