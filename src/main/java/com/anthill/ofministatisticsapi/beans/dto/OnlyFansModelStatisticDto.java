package com.anthill.ofministatisticsapi.beans.dto;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelStatisticDto {

    private OnlyFansModel model;
    private Statistic current;
    private List<Statistic> historical;
}
