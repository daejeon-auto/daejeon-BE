package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.WithMockCustomUser;
import com.pcs.daejeon.entity.Like;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.repository.LikeRepository;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void 이미_좋야요를_눌렀을때() {

        Member member = memberRepository.getLoginMember();
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

        Member member = memberRepository.getLoginMember();
        Post helloWorld = new Post("hello world", member.getSchool());
        postRepository.save(helloWorld);

        boolean valid = likeRepository.validLike(member, helloWorld.getId());

        assertThat(valid).isFalse();
    }

}