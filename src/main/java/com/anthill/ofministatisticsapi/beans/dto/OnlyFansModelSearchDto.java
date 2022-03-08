package com.anthill.ofministatisticsapi.beans.dto;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelSearchDto {

    private OnlyFansModel model;
    private Statistic statistic;
}
