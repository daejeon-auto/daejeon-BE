package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.basic.BasicTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "school_id")
    private Long id;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "school")
    @JsonIgnore
    private List<Member> student;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "school")
    @JsonIgnore
    private List<Post> post;

    private String name;
    private String locate;
    private String code;
    private String locationCode;

    private String instaId;
    private String instaPwd;

    public School(String name, String locate, String code, String locationCode, String instaId, String instaPwd) {
        this.name = name;
        this.locate = locate;
        this.code = code;
        this.instaId = instaId;
        this.instaPwd = instaPwd;
        this.locationCode = locationCode;
    }
}
