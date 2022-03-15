package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentStatisticService {

    private final DataScrapperService scrapperService;
    private final StatisticRepos statisticRepos;

    public CurrentStatisticService(DataScrapperService scrapperService, StatisticRepos statisticRepos) {
        this.scrapperService = scrapperService;
        this.statisticRepos = statisticRepos;
    }

    public CurrentStatisticDto updateByModel(OnlyFansModel model){
        try{
            var update = scrapperService.getStatistic(model.getUrl());

            update.setModel(model);
            statisticRepos.save(update);

            var lastGlobalPointOptional =
                    statisticRepos.findLastGlobalPointByModel(model.getId());

            var today = lastGlobalPointOptional
                    .map(lastGlobalPoint ->
                            Statistic.subtract(update, lastGlobalPoint))
                    .orElseGet(() -> {
                       var todayFirst = statisticRepos.findTodayFirstByModel(model.getId());
                       return Statistic.subtract(update, todayFirst.get());
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

            return new CurrentStatisticDto(model.getName(), update, today, yesterday, week, month);
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
        } else if (statistics.size() == 1){

            return statistics.get(0);
        } else {

            return new Statistic();
        }
    }
}
