package com.anthill.ofministatisticsapi.beans.dto.onlyFansModel;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelItemDto {

    private OnlyFansModel model;
    private Statistic statistic;
}
