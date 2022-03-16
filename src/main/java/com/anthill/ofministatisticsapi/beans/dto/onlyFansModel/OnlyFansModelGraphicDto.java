package com.anthill.ofministatisticsapi.beans.dto.onlyFansModel;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OnlyFansModelGraphicDto {

    private OnlyFansModel model;
    private Statistic current;
    private List<Statistic> graphical;
}
