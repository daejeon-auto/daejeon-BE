package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.nio.ByteBuffer;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Transactional
public class ReferCode extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refer_code_id")
    private Long id;

    private String code;

    @OneToOne(mappedBy = "usedCode")
    private Member usedBy = null;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member createdBy;

    private boolean isUsed;

    public void generateCode(Member member) {
        this.code = makeShortUUID();
        this.createdBy = member;
    }

    private static String makeShortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public void setIsUsed() {
        this.isUsed = true;
    }
}
