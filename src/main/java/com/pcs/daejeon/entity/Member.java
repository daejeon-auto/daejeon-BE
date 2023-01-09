package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.type.AuthType;
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
    private String name;
    @Column(nullable = false)
    private String birthDay;
    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;

    @Column(nullable = false)
    private String studentNumber;

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

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.")
    @Column(name = "login_id")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @OneToMany(mappedBy = "likedBy")
    @JsonIgnore
    private List<Like> like = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "refer_code_id")
    @JsonIgnore
    private ReferCode usedCode;

    @OneToMany(mappedBy = "createdBy",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<ReferCode> referCodes = new ArrayList<>();

    @OneToMany(mappedBy = "reportedBy",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "school_id")
    private School school;

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void setRole(RoleTier role) {
        this.role = role;
    }

    public void useCode(ReferCode usedCode) {
        this.usedCode = usedCode;
        usedCode.setIsUsed();
    }

    public Member(String name, String birthDay, String phoneNumber, String studentNumber, String password, String loginId, AuthType authType, School school) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.studentNumber = studentNumber;
        this.password = password;
        this.loginId = loginId;
        this.authType = authType;
        this.school = school;
        this.role = RoleTier.ROLE_TIER0;
        this.memberType = MemberType.PENDING;
    }
}
