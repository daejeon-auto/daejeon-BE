package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.entity.type.AuthType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

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

    @Value("0")
    @Column(nullable = false)
    @Size(max = 3)
    private int referCodeCount;

    @Value("TIER0")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleTier role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Value("PENDING")
    private MemberType memberType;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> post;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.")
    @Column(name = "login_id")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    public Member(String name, String birthDay, String phoneNumber, String studentNumber, MemberType memberType) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.studentNumber = studentNumber;
        this.memberType = memberType;
    }
    public Member(String name, String birthDay, String phoneNumber, String studentNumber, MemberType memberType, String password, String loginId) {
        this.name = name;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.studentNumber = studentNumber;
        this.memberType = memberType;
        this.password = password;
        this.loginId = loginId;
    }
}
