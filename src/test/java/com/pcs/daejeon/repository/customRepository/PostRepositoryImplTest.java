package com.pcs.daejeon.repository.customRepository;

import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @BeforeEach
    public void testDataInsert() {
        for (int i = 1; i <= 100; i++) {
            em.persist(new Post("Test.dev" + i));
        }
    }

    @Test
    public void paging() {
        Pageable page = PageRequest.of(1, 20);
        QueryResults<Post> postPage = postRepository.pagingPost(page);

        assertThat(postPage.getResults().get(19).getDescription()).isEqualTo("Test.dev80");
        assertThat(postPage.getTotal()).isEqualTo(100);
        assertThat(postPage.getLimit()).isEqualTo(20);
    }
}