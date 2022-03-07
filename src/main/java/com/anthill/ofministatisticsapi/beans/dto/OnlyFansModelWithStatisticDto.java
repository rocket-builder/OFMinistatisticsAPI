package com.anthill.ofministatisticsapi.beans.dto;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelWithStatisticDto {

    private OnlyFansModel model;
    private Statistic statistic;
}
