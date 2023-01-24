package com.pcs.daejeon.service;

import com.pcs.daejeon.WithMockCustomUser;
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
    EntityManager em;

    @BeforeEach
    public void initData() {
        School school = new School(
                "테스트학교",
                "지역",
                "인스타아이디",
                "인스타비밀번호"
        );
        schoolRepository.save(school);
        for (int i = 0; i < 100; i++) {
            postRepository.save(new Post("test value " + i, school));
        }
    }

    @Test
    @DisplayName("글 작성 성공")
    void writePost200() {
        Long postId = postService.writePost("test글 작성");

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
    @DisplayName("글 작성 실패 - 욕설 포함")
    void writePost400BadWord() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.writePost("시발----------"));
    }

    @Test
    @DisplayName("글 삭제 성공")
    void deletePost200() {
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
    void acceptPost200() {
        Long postId = postService.writePost("write post 1");

        postService.deletePost(postId);
        Post postById = postService.findPostById(postId);
        assertThat(postById.getPostType()).isEqualTo(PostType.DELETE);

        postService.acceptPost(postId);
        assertThat(postById.getPostType()).isEqualTo(PostType.ACCEPTED);
    }

    @Test
    @DisplayName("글 승인 실패 - 게시글 없음")
    void acceptPost404() {
        Assertions.assertThrows(IllegalStateException.class, () -> postService.acceptPost(0L));
    }

    @Test
    @DisplayName("글 가져오기 성공")
    void findPagedPost() {
        List<Tuple> post = postService.findPagedPost(PageRequest.of(0, 15)).getContent();

        Assertions.assertFalse(post.isEmpty());
        assertThat(post.size()).isEqualTo(15);
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
        Page<Post> pagedRejectedPost = postService.findPagedRejectedPost(PageRequest.of(0, 15), null, null);

        assertThat(pagedRejectedPost.getTotalElements()).isEqualTo(0);
    }

    // TODO: 게시글 갖고 올 때 report가 5개 이상이어야 함. 신고는 계정당 하나임으로 해결법 모색
    // searchPost까지
//    @Test
//    @DisplayName("신고된 게시글 가져오기 - 게시물 있음")
//    void findPagedRejectedPost200() {
//        Page<Post> pagedRejectedPost = postService.findPagedRejectedPost(PageRequest.of(0, 15), null, null);
//    }

//    @Test
//    @DisplayName("신고된 게시글 가져오기 - 검색(memberId, reportCound)")
//    void findPagedRejectPost_search() {}

    @Test
    @DisplayName("미신고 게시글 검색 성공")
    void searchPost200() {
        Page<Post> posts = postService.searchPost(PageRequest.of(0, 15), memberRepository.getLoginMember().getId(), null);

        assertThat(posts.getSize()).isEqualTo(15);
    }
}