package com.anthill.ofministatisticsapi.beans.dto;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelStatisticDto {

    private OnlyFansModel model;
    private Statistic current;
    private List<Statistic> historical;
}
