package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.Log;
import com.pcs.daejeon.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findAllBySchool(School school);
}
