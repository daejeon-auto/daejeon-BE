package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.type.PostType;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
public class Post extends BasicEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Size(max = 100, min = 5)
    @NotNull
    private String description;

    private int reported;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member createByMember;

    @OneToOne(mappedBy = "post")
    private Like like;

    public Post() {}

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public void addReported() {
        this.reported++;
    }

    public Post(String description) {
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        this.postType = PostType.ACCEPTED;
    }
}
