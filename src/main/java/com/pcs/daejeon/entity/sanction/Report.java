package com.pcs.daejeon.entity.sanction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Post;
import com.pcs.daejeon.entity.basic.BasicTime;
import com.pcs.daejeon.entity.type.ReportType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BasicTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id")
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String reason;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member reportedBy;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post reportedPost;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    public Report(String reason, Member reportedBy, Post reportedPost, ReportType reportType) {
        this.reason = reason;
        this.reportedBy = reportedBy;
        this.reportedPost = reportedPost;
        this.reportType = reportType;
        reportedAt = LocalDateTime.now();
    }
}
