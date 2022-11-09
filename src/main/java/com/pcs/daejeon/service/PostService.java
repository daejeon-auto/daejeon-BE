package com.pcs.daejeon.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.instagram4j.instagram4j.IGClient;
import com.pcs.daejeon.dto.ApiResponseDto;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

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

    public void uploadInstagram(String description) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://api.imgbun.com/png?key=619da368dc3f53a8d00e8a39667cc860&text="+ description +"&color=000000&size=20").build();

            ResponseEntity<Map> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Object link = Objects.requireNonNull(response.getBody()).get("direct_link");

            uploadImage(link.toString());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException("convert api error");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void uploadImage(String imageUrl) throws IOException, URISyntaxException {
        IGClient client = IGClient.builder()
                .username("pcs_daejeon")
                .password("pcs13579")
                .login();

        URL url = new URL(imageUrl);
        File imageFile = Paths.get(url.toURI()).toFile();

        client.actions()
                .timeline()
                .uploadPhoto(imageFile, "컴대전 입니다.")
                .join();
    }
}
