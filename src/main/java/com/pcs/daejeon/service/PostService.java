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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final IGClient client;

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

    public void addLike(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("post not found");
        }

        int likedCount = post.get().addLiked();

        if (likedCount == 3) {
            uploadInstagram(post.get().getDescription());
        }
    }

    public void textToImage(String description) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://api.imgbun.com/jpg?key=619da368dc3f53a8d00e8a39667cc860&size=40&text="+ description +"&color=000000").build();

            ResponseEntity<Map> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Object link = Objects.requireNonNull(response.getBody()).get("direct_link");

            saveImage(link.toString());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException("convert api error");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImage(String imageUrl) throws IOException, URISyntaxException {
        try {
            URL url = new URL(imageUrl);
            BufferedImage img = ImageIO.read(url);
            File file = new File(System.getProperty("user.dir")+"/src/textImage.jpg");
            boolean write = ImageIO.write(img, "jpg", file);
            System.out.println("write = " + write);
            uploadToInstagram(file);
        } catch (IllegalArgumentException e) {
            System.out.println("e = " + e);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadToInstagram(File file) throws IOException {
        byte[] imgData = Files.readAllBytes(file.toPath());
        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
        String id = client.sendRequest(uploadReq).join().getUpload_id();
        IGRequest<MediaResponse.MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
                new MediaConfigureTimelineRequest.MediaConfigurePayload().upload_id(id).caption("👍👍"));
        MediaResponse.MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();
    }
}
