package com.anthill.ofministatisticsapi.beans.dto.onlyFansModel;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CalculatedStatisticDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelCalculatedDto {

    private OnlyFansModel model;

    private Statistic current;

    private List<CalculatedStatisticDto> calculatedStatistics;
}
