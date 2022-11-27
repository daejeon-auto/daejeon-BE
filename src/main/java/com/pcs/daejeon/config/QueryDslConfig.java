package com.pcs.daejeon.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

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
