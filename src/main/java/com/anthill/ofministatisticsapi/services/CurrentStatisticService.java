package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelCalculatedStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelGraphicDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CalculatedStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.enums.DateUnit;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrentStatisticService {

    private final CalculatedStatisticService calculatedStatisticService;
    private final DataScrapperService scrapperService;
    private final StatisticRepos statisticRepos;

    public CurrentStatisticService(CalculatedStatisticService calculatedStatisticService,
                                   DataScrapperService scrapperService, StatisticRepos statisticRepos) {
        this.calculatedStatisticService = calculatedStatisticService;
        this.scrapperService = scrapperService;
        this.statisticRepos = statisticRepos;
    }

    public OnlyFansModelCalculatedStatisticDto getCurrentWithCalculated(OnlyFansModel model, Date start){
        var statistic = model.getStatistics();

        var historical = statistic.stream()
                .filter(s -> s.getMoment().after(start) && s.isGlobalPoint())
                .collect(Collectors.toList());

        var calculated = calculatedStatisticService.calculate(historical);

        var current = statistic.size() > 0? statistic.get(statistic.size() - 1) : null;

        return OnlyFansModelCalculatedStatisticDto.builder()
                .model(model)
                .historicalCalculated(calculated)
                .current(current)
                .build();
    }

    public List<CalculatedStatisticDto> getCalculatedGraphicData(OnlyFansModel model, DateUnit unit, int count){
        List<Statistic> graphic = new ArrayList<>();
        switch (unit){
            case MONTH:
                graphic = statisticRepos.findLastGlobalPointsByModelAndMonthCount(model.getId(), count);
                break;
            case DAY:
                graphic = statisticRepos.findLastGlobalPointsByModelAndDaysCount(model.getId(), count);
                break;
        }

        return calculatedStatisticService.calculate(graphic);
    }

    public OnlyFansModelGraphicDto getWithCalculatedGraphic(OnlyFansModel model, DateUnit unit, int count)
            throws CannotGetStatisticException, ResourceNotFoundedException {
        var current = scrapperService.getStatistic(model.getUrl());

        var calculated = getCalculatedGraphicData(model, unit, count + 1);

        return OnlyFansModelGraphicDto.builder()
                .model(model)
                .current(current)
                .graphical(calculated)
                .build();
    }

    public CurrentStatisticDto updateByModel(OnlyFansModel model) {
        try{
            Statistic updateOrLast = null;
            try {
                updateOrLast = scrapperService.getStatistic(model.getUrl());

                updateOrLast.setModel(model);
                statisticRepos.save(updateOrLast);
            } catch (ResourceNotFoundedException ex){
                updateOrLast = statisticRepos.findLastByModel(model.getId())
                        .orElseGet(Statistic::new);
            }
            var update = updateOrLast;

            var lastGlobalPointOptional =
                    statisticRepos.findLastGlobalPointByModel(model.getId());

            var today = lastGlobalPointOptional
                    .map(lastGlobalPoint ->
                            Statistic.subtract(update, lastGlobalPoint))
                    .orElseGet(() -> {
                       var todayFirstOptional = statisticRepos.findTodayFirstByModel(model.getId());
                       return todayFirstOptional
                               .map(statistic ->
                                       Statistic.subtract(update, statistic))
                               .orElseGet(Statistic::new);
                    });

            var lastYesterdayGlobalPointOptional =
                    statisticRepos.findLastYesterdayGlobalPointByModel(model.getId());

            var yesterday = lastYesterdayGlobalPointOptional
                    .map(lastYesterdayGlobalPoint ->
                            lastGlobalPointOptional
                                    .map(lastGlobalPoint ->
                                            Statistic.subtract(lastGlobalPoint, lastYesterdayGlobalPoint))
                                    .orElseGet(() ->
                                            Statistic.subtract(update, lastYesterdayGlobalPoint)))
                    .orElseGet(Statistic::new);

            var week = subtractLastFromFirst(
                    statisticRepos.findLastWeekByModel(model.getId()));

            var month = subtractLastFromFirst(
                    statisticRepos.findLastMonthByModel(model.getId()));

            return new CurrentStatisticDto(model.getName(), updateOrLast, today, yesterday, week, month);
        } catch (CannotGetStatisticException ex){
            ex.printStackTrace();

            return new CurrentStatisticDto();
        }
    }

    private Statistic subtractLastFromFirst(List<Statistic> statistics){
        if (statistics.size() >= 2){
            var first = statistics.get(0);
            var last = statistics.get(statistics.size()-1);

            return Statistic.subtract(last, first);
        } else {

            return new Statistic();
        }
    }
}
