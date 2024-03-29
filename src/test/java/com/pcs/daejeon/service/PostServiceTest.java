package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@WithMockCustomUser
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    Util util;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void initData() {
        School school = util.getLoginMember().getSchool();
        for (int i = 0; i < 100; i++) {
            postRepository.save(new Post("test value " + i, school));
        }
    }

    @Test
    @DisplayName("글 작성 성공")
    void writePost200() throws MethodArgumentNotValidException {
        Long postId = postService.writePost("test글 작성----------");

        Optional<Post> post = postRepository.findById(postId);

        Assertions.assertFalse(post.isEmpty());
    }

    @Test
    @DisplayName("글 작성 실패 - 글자수 초과")
    void writePost400Len() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> postService.writePost(
                            "test글 작성------------------------" +
                                    "-------------------------------------" +
                                    "-----------------------------------------"));
    }

    @Test
    @DisplayName("글 삭제 성공")
    void deletePost200() throws MethodArgumentNotValidException {
        Long postId = postService.writePost("test post 1");

        em.flush();
        em.clear();

        postService.deletePost(postId);
        Post postById = postService.findPostById(postId);
        assertThat(postById.getPostType()).isEqualTo(PostType.DELETE);
    }

    @Test
    @DisplayName("글 삭제 실패 - 게시글 없음")
    void deletePost404() {
        Assertions.assertThrows(IllegalStateException.class, () -> postService.deletePost(0L));
    }

    @Test
    @DisplayName("글 승인 성공")
    void acceptPost200() throws MethodArgumentNotValidException {
        Long postId = postService.writePost("write post 1");

        postService.deletePost(postId);
        Post postById = postService.findPostById(postId);
        assertThat(postById.getPostType()).isEqualTo(PostType.DELETE);

        postService.acceptPost(postId);
        assertThat(postById.getPostType()).isEqualTo(PostType.SHOW);
    }

    @Test
    @DisplayName("글 승인 실패 - 게시글 없음")
    void acceptPost404() {
        Assertions.assertThrows(IllegalStateException.class, () -> postService.acceptPost(0L));
    }

    @Test
    @DisplayName("글 가져오기 성공")
    void findPagedPost() {
        Page<Tuple> pagedPost = postService.findPagedPost(PageRequest.of(0, 15),
                util.getLoginMember().getSchool().getId());

        Assertions.assertTrue(pagedPost.hasContent());
        assertThat(pagedPost.getContent().size()).isEqualTo(15);
    }

    @Test
    @DisplayName("로그인한 사용자가 작성한 글 리스트 - 성공")
    void findPagedPostByMemberId() {
        List<Post> content = postService.findPagedPostByMemberId(PageRequest.of(0, 15)).getContent();

        int i = 99;
        for (Post post : content) {
            assertThat(post.getDescription()).isEqualTo("test value " + i--);
        }
    }

    @Test
    @DisplayName("신고된 게시글 가져오기 - 게시물 없음")
    void findPagedRejectedPost404() {
        Page<Post> pagedRejectedPost = postService.findPagedRejectedPost(PageRequest.of(0, 15),
                null, null);

        assertThat(pagedRejectedPost.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("미신고 게시글 검색 성공")
    void searchPost200() {
        Page<Post> posts = postService.searchPost(PageRequest.of(0, 15), util.getLoginMember().getId(), null);

        assertThat(posts.getSize()).isEqualTo(15);
    }
}