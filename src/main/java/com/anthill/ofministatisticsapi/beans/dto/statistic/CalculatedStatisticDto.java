package com.anthill.ofministatisticsapi.beans.dto.statistic;

import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CalculatedStatisticDto {

    private Statistic calculated;
    private Statistic statistic;
}
