package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.QPost;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.PostType;
import com.pcs.daejeon.repository.MemberRepository;
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
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    PageRequest pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void initData() {
        for (int i = 0; i < 100; i++) {
            postRepository.save(
                    new Post("test value " + i,
                            new School(
                                    "테스트학교",
                                    "지역",
                                    "인스타아이디",
                                    "인스타비밀번호"
                            )));
        }
    }

    @Test
    public void unloggedInPagingPost() {
        Page<Tuple> post = postRepository.pagingPost(pageable);

        assertThat(post.getTotalElements()).isEqualTo(100L);
        assertThat(post.getTotalPages()).isEqualTo(100 / 10);

        for (int i = 0; i < post.getContent().size(); i++) {
            assertThat(post.getContent().get(i).get(QPost.post).getDescription()).isEqualTo("test value " + (99-i));
        }
    }

    @Test
    public void 신고된_글_리스트() {
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

    @Test
    public void 내가_쓴_글_리스트() {
        Member member = memberRepository.getLoginMember();
        Post post = new Post("test글 작성", member.getSchool());
        Post save = postRepository.save(post);

        em.flush();
        em.clear();

        Member loginMember = memberRepository.getLoginMember();
        Page<Post> posts = postRepository.pagingPostByMemberId(pageable, loginMember);

        Post findPost = posts.getContent().get(0);

        assertThat(save.getId()).isEqualTo(findPost.getId());
    }
}