package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.PostRepository;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback
@WithMockCustomUser
class PostRepositoryImplTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void initData() {
        for (int i = 0; i < 100; i++) {
            postRepository.save(new Post("test value " + i));
        }
    }

    @Test
    public void unloggedInPagingPost() {
        PageRequest page = PageRequest.of(0, 10);
        Page<Tuple> post = postRepository.pagingPost(page);

        assertThat(post.getTotalElements()).isEqualTo(100L);
        assertThat(post.getTotalPages()).isEqualTo(100 / 10);

        for (int i = 0; i < post.getContent().size(); i++) {
            assertThat(post.getContent().get(i).get(QPost.post).getDescription()).isEqualTo("test value " + (99-i));
        }
    }

    @Test
    public void 신고된_글_리스트() {
        PageRequest pageable = PageRequest.of(0, 10);

        // == reject post 생성 ==
        Page<Tuple> tuples = postRepository.pagingPost(pageable);

        for (Tuple tuple : tuples.getContent()) {
            Objects.requireNonNull(tuple.get(QPost.post)).setPostType(PostType.REJECTED);
        }
        // == reject post 생성 ==

        Page<Post> posts = postRepository.pagingRejectPost(pageable, null, null);

        List<Post> content = posts.getContent();

        for (int i = 0; i < content.size(); i++) {
            assertThat(content.get(i).getDescription()).isEqualTo("test value " + (99-i));
            assertThat(content.get(i).getPostType()).isEqualTo(PostType.REJECTED);
        }
    }

}