package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback
class PostRepositoryImplTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    @WithMockCustomUser
    public void initData() {
        for (int i = 0; i < 100; i++) {
            postRepository.save(new Post("test value " + i));
        }
    }

    @Test
    @WithMockCustomUser
    public void unloggedInPagingPost() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Tuple> post = postRepository.pagingPost(page);

        assertThat(post.getTotalElements()).isEqualTo(100L);
        assertThat(post.getTotalPages()).isEqualTo(100 / 10);

        for (int i = 0; i < post.getContent().size(); i++) {
            assertThat(post.getContent().get(i).get(QPost.post).getDescription()).isEqualTo("test value " + (99-i));
        }
    }

}