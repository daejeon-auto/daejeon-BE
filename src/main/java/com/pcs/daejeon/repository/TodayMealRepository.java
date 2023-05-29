package com.pcs.daejeon.repository;


import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.TodayMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodayMealRepository extends JpaRepository<TodayMeal, Long> {

    TodayMeal findBySchool(School school);
}
