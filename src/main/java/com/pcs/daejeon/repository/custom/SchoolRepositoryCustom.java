package com.pcs.daejeon.repository.custom;

import com.pcs.daejeon.entity.School;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepositoryCustom {

    boolean validInstaId(String id);

    boolean valiSchool(School school);

}
