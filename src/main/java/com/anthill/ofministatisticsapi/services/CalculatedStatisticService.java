package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CalculatedStatisticDto;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculatedStatisticService {

    public List<CalculatedStatisticDto> calculate(List<Statistic> historical){
        return StreamEx.of(historical)
                .pairMap((p, n) ->
                        CalculatedStatisticDto.builder()
                                .calculated(Statistic.subtract(p, n))
                                .statistic(n)
                                .build())
                .collect(Collectors.toList());
    }
}
