package com.anthill.ofministatisticsapi.beans.dto;

import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CurrentStatisticDto {

    private String name;
    private Statistic current, today, yesterday, week, month;
}
