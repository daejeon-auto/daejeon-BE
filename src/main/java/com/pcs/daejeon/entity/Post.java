package com.pcs.daejeon.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
public class Post extends BasicTime {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Size(max = 100, min = 10)
    private String description;

    private int liked;
}
