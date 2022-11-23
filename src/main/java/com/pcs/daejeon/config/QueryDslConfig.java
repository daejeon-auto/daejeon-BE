package com.pcs.daejeon.config;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class QueryDslConfig {
    @Autowired
    EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

//    @Bean
//    public IGClient igClient() throws IGLoginException {
//        IGClient client = IGClient.builder()
//                .username("pcs_daejeon")
//                .password("pcs13579")
//                .login();
//
//        return client;
//    }
}
