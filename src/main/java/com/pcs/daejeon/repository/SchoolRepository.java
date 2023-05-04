package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.custom.SchoolRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SchoolRepository extends JpaRepository<School, Long>, SchoolRepositoryCustom {

    List<School> findAllByUploadMealIsTrue();
}
