package com.pcs.daejeon.entity;

import com.pcs.daejeon.entity.basic.BasicTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity @Getter
@NoArgsConstructor
public class TodayMeal extends BasicTime {

    public TodayMeal(School school) {
        this.school = school;
    }

    @Column(name = "today_meal_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<String> breakfast;

    @ElementCollection
    private List<String> lunch;

    @ElementCollection
    private List<String> dinner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    public void setBreakfast(List<String> breakfast) {
        this.breakfast = breakfast;
    }

    public void setLunch(List<String> lunch) {
        this.lunch = lunch;
    }

    public void setDinner(List<String> dinner) {
        this.dinner = dinner;
    }
}
