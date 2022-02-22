package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentStatisticService {

    private final StatisticRepos statisticRepos;


    public CurrentStatisticService(StatisticRepos statisticRepos) {
        this.statisticRepos = statisticRepos;
    }

    public CurrentStatisticDto getByModel(OnlyFansModel model){
        var current = statisticRepos.findLastByModel(model.getId());
        if(current.isEmpty()){
            return new CurrentStatisticDto();
        }

        var stats = statisticRepos.findTodayByModel(model.getId());
        var today = calculateStatistic(stats);

        stats = statisticRepos.findYesterdayByModel(model.getId());
        var yesterday = calculateStatistic(stats);

        stats = statisticRepos.findLastWeekByModel(model.getId());
        var week = calculateStatistic(stats);

        stats = statisticRepos.findLastMonthByModel(model.getId());
        var month = calculateStatistic(stats);

        return new CurrentStatisticDto(model.getName(), current.get(), today, yesterday, week, month);
    }

    private Statistic calculateStatistic(List<Statistic> statistics){
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
