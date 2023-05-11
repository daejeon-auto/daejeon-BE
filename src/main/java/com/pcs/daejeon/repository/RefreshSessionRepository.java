package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;

@Component
/**
 * tomcat 내장 메모리 세션
 */
public interface RefreshSessionRepository extends CrudRepository<Session, String> {
}