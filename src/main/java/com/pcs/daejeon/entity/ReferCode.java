package com.pcs.daejeon.entity;

import com.pcs.daejeon.entity.member.DirectMember;
import com.pcs.daejeon.entity.member.IndirectMember;
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
    private IndirectMember usedBy = null;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private DirectMember createdBy;

    private boolean isUsed;

    public void generateCode(DirectMember member) {
        this.code = makeShortUUID();
        this.createdBy = member;
        member.getReferCodes().add(this);
    }

    private static String makeShortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public void useCode(IndirectMember member) {

        this.isUsed = true;
        this.usedBy = member;
    }
}
