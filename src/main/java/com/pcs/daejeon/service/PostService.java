package com.pcs.daejeon.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.IGRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadPhotoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import com.github.instagram4j.instagram4j.responses.media.RuploadPhotoResponse;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.PostType;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
//    private final IGClient client;

    public Long writePost(String description) {
//        boolean isOk = isBadDesc(description);
//        if (!isOk) {
//            throw new IllegalStateException("bad words");
//        }
        Post save = postRepository.save(new Post(description));
        return save.getId();
    }

    /**
     *  check is it bad words in description
     * @return 욕 == true || 욕 아님 == false
     */
    private boolean isBadDesc(String description) {

        ResponseEntity<Map> resultMap = callValidDescApi(description);

        if (!resultMap.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(resultMap.getBody()).toString() == "욕";
        } else {
            throw new IllegalStateException("no response for valid description");
        }
    }

    /**
     * api 관련 부분
     * @param description
     * @return
     */
    private static ResponseEntity<Map> callValidDescApi(String description) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("text", description);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("localhost:5000/chk").build();

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Map.class);
            return resultMap;
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

        if (likedCount == 1) {
            drawImage(post.get().getDescription());
        }
    }

    private void drawImage(String description) throws IOException, URISyntaxException {
        try {
            StringBuffer text = new StringBuffer(description);
            for (int i = 0; i < description.length(); i++) {
                if ((i % 11) == 0) {
                    text.insert(i, "\n");
                }
            }
            System.out.println(text);
            String [] lines = String.valueOf(text).split("\n");

            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            Font font = new Font("Nanum", Font.PLAIN, 40);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int width = fm.stringWidth(String.valueOf(text.toString()));

            img = new BufferedImage(width, fm.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g2d = img.createGraphics();
            int lineHeight = g2d.getFontMetrics().getHeight();
            for(int lineCount = 0; lineCount < lines.length; lineCount++){ //lines from above
                int xPos = 100;
                int yPos = 100 + lineCount * lineHeight;
                String line = lines[lineCount];
                g2d.drawString(line, xPos, yPos);
            }
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            g2d.setColor(Color.BLACK);
            g2d.setBackground(Color.white);
            g2d.dispose();

            boolean jpg = ImageIO.write(img, "png", new File(System.getProperty("user.dir")+"/src/graphic.png"));

            convertPngToJpg();
        } catch (IllegalArgumentException e) {
            System.out.println("e = " + e);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void uploadToInstagram() throws IOException {
//        File file = new File(System.getProperty("user.dir")+"/src/graphic.png");
//        byte[] imgData = Files.readAllBytes(file.toPath());
//        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
//        String id = client.sendRequest(uploadReq).join().getUpload_id();
//        IGRequest<MediaResponse.MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
//                new MediaConfigureTimelineRequest.MediaConfigurePayload().upload_id(id).caption("👍👍"));
//        MediaResponse.MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();
//    }

    private void convertPngToJpg() throws IOException {
        Path source = Paths.get(System.getProperty("user.dir")+"/src/graphic.png");
        Path target = Paths.get(System.getProperty("user.dir")+"/src/textImage.jpg");

        BufferedImage originalImage = ImageIO.read(source.toFile());

        // jpg needs BufferedImage.TYPE_INT_RGB
        // png needs BufferedImage.TYPE_INT_ARGB

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
