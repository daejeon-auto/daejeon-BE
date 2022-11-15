package com.pcs.daejeon.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.nio.ByteBuffer;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ReferCode extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refer_code_id")
    private Long id;

    private String code;

    @OneToOne(mappedBy = "usedCode")
    private Member usedBy = null;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member createdBy;

    private boolean isUsed;

    public void generateCode(Member member) {
        this.code = makeShortUUID();
        this.createdBy = member;
        member.getReferCodes().add(this);
    }

    private static String makeShortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public void useCode(Member member) {

        this.isUsed = true;
        this.usedBy = member;
    }
}
