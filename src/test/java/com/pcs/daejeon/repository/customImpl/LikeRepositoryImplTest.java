package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.Like;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.LikeRepository;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@WithMockCustomUser
@Transactional
@Rollback
@ActiveProfiles("test")
class LikeRepositoryImplTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    Util util;

    @Test
    public void 이미_좋야요를_눌렀을때() {

        Member member = util.getLoginMember();
        schoolRepository.save(member.getSchool());
        Post helloWorld = new Post("hello world", member.getSchool());
        postRepository.save(helloWorld);

        Optional<Post> post = postRepository.findById(helloWorld.getId());

        Like like = new Like(member, post.get());

        likeRepository.save(like);

         boolean valid = likeRepository.validLike(member, helloWorld.getId());

        assertThat(valid).isTrue();
    }

    @Test
    public void 좋아요를_처음_등록할때() {

        Member member = util.getLoginMember();
        schoolRepository.save(member.getSchool());
        Post helloWorld = new Post("hello world", member.getSchool());
        postRepository.save(helloWorld);

        boolean valid = likeRepository.validLike(member, helloWorld.getId());

        assertThat(valid).isFalse();
    }

}