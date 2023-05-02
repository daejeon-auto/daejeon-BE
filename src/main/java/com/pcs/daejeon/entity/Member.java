package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.basic.BasicTime;
import com.pcs.daejeon.entity.sanction.Punish;
import com.pcs.daejeon.entity.sanction.Report;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleTier role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

    @JsonIgnore
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "createByMember")
    private List<Post> post;

    @NotEmpty(message = "아이디는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "아이디는 4자 이상, 16자 이하로 입력해주세요.")
    @Column(name = "login_id")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @OneToMany(mappedBy = "likedBy")
    @JsonIgnore
    private final List<Like> like = new ArrayList<>();

    @OneToMany(mappedBy = "reportedBy",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private final List<Report> reports = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "member",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Punish> punish;

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void setRole(RoleTier role) {
        this.role = role;
    }

    public Member(String phoneNumber, String password, String loginId, School school) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.loginId = loginId;
        this.school = school;
        this.role = RoleTier.ROLE_TIER0;
        this.memberType = MemberType.ACCEPT;
    }
}
