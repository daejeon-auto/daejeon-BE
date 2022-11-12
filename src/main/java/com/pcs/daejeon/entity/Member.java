package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.type.AuthType;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;
    private String birthDay;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private String studentNumber;

    @ColumnDefault("0")
    private int referCodeCount;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> post;
}
