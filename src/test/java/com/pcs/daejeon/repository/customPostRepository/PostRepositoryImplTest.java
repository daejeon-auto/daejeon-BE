package com.pcs.daejeon.repository.customPostRepository;

import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostRepositoryImplTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @BeforeEach
    public void testDataInsert() {
        for (int i = 1; i <= 30; i++) {
            em.persist(new Post("Test.dev" + i));
        }
    }

//    @Test
//    public void paging() {
//        Pageable page = PageRequest.of(1, 20);
//        QueryResults<Post> postPage = postRepository.pagingPost(page);
//
//        for (Post result : postPage.getResults()) {
//            System.out.println("result = " + result.getDescription());
//        }
//
//        assertThat(postPage.getTotal()).isEqualTo(30);
//        assertThat(postPage.getLimit()).isEqualTo(20);
//    }

    @Test
    public void write() {
        Long postId = postService.writePost("this is test value");

        Post postById = postService.findPostById(postId);

        assertThat(postById.getDescription()).isEqualTo("this is test value");
    }
}