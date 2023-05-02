package com.pcs.daejeon.entity.sanction;

import com.pcs.daejeon.entity.basic.BasicEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportBullying extends BasicEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_bullying_id")
    private Long id;

    private String reason;

    private String punishmentLevel;

    public ReportBullying(String reason, String punishmentLevel) {
        this.reason = reason;
        this.punishmentLevel = punishmentLevel;
    }
}
