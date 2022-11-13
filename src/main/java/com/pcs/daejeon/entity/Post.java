package com.pcs.daejeon.entity;

import com.pcs.daejeon.entity.type.PostType;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

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

    private int liked;

    public Post() {}

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public int addLiked() {
        this.liked++;
        return this.liked;
    }

    public Post(String description) {
        this.description = description;
    }
    public boolean validDescription() {
        return description == null || Objects.requireNonNull(description).length() < 5;
    }

    @PrePersist
    public void prePersist() {
        this.postType = PostType.ACCEPTED;
    }
}
