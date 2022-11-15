package com.pcs.daejeon.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like")
public class Like extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member likedBy;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Like(Member likedBy, Post post) {
        this.likedBy = likedBy;
        this.post = post;
    }

}
