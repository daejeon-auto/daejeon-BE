package com.pcs.daejeon.repository;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;

@Component
/**
 * tomcat 내장 메모리 세션
 */
public class RefreshSessionRepository {

    private final LinkedHashMap<String, Date> session = new LinkedHashMap<>();

    public void addData(String key, Date expireDate) {
        session.put(key, expireDate);
    }

    public Date getData(String key) {
        return session.get(key);
    }

    public LinkedHashMap<String, Date> getSession() {
        return session;
    }

    public void removeData(String key) {
        session.remove(key);
    }
}