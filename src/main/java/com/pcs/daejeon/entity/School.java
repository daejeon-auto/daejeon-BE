package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.entity.basic.BasicTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

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

    private boolean uploadMeal = false;

    private String instaId = null;
    private String instaPwd = null;

    private final String salt = null;

    private boolean isAbleInstagram = false;

    public School(String name, String locate, String code,
                  String locationCode) {
        this.name = name;
        this.locate = locate;
        this.code = code;
        this.locationCode = locationCode;
    }

    public void setIsableInstagram(boolean isAbleInstagram) {
        this.isAbleInstagram = isAbleInstagram;
    }

    /**
     * isAbleInstagram이 true일 때만 설정 가능
     * @param uplodaMeal : 급식 업로드 할건지
     */
    public void setUploadMeal(boolean uplodaMeal) {
        if (isAbleInstagram) this.uploadMeal = uplodaMeal;
    }

    public void updateInstagram(String instaId, String instaPwd) throws Exception {
        String salt = UUID.randomUUID().toString();

        String encryptedInstaId = Util.encrypt(instaId, salt);
        String encryptedInstaPwd = Util.encrypt(instaPwd, salt);

        this.instaId = encryptedInstaId;
        this.instaPwd = encryptedInstaPwd;
    }
}
