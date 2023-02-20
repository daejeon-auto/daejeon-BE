package com.pcs.daejeon.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    @Id @Column(name = "log_id")
    private Long id;

    private LogLevel logLevel;

    private String log_data;

    private LocalDateTime logDate;

    private String sqlState;

    private String methodCall;

    private String stackTrace;

    @ManyToOne
    @JoinColumn(name = "log")
    private School school;
}
