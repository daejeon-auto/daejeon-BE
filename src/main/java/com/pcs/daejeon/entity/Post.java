package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.basic.BasicEntity;
import com.pcs.daejeon.entity.type.PostType;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post extends BasicEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Size(max = 100, min = 10)
    @NotNull
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member createByMember;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<Like> like;

    @OneToMany(mappedBy = "reportedPost", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    // 어느 학교의 게시글인지 확인
    @ManyToOne()
    @JoinColumn(name = "school_id")
    private School school;

    public Post() {}

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public Post(String description, School school) {
        this.description = description;
        this.school = school;
    }

    @PrePersist
    public void prePersist() {
        this.postType = PostType.ACCEPTED;
    }
}
