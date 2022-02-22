package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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

        var today = calculateStatistic(
                statisticRepos.findLastDayByModel(model.getId()));

        var week = calculateStatistic(
                statisticRepos.findLastWeekByModel(model.getId()));

        var month = calculateStatistic(
                statisticRepos.findLastMonthByModel(model.getId()));

        //TODO add yesterday
        return new CurrentStatisticDto(model.getName(), current.get(), today, today, week, month);
    }

    private Statistic calculateStatistic(List<Statistic> statistics){
        if (statistics.size() >= 2){
            var first = statistics.get(0);
            var last = statistics.get(statistics.size()-1);

            last.subtract(first);
            return last;
        } else if (statistics.size() == 1){

            return statistics.get(0);
        } else {

            return new Statistic();
        }
    }
}
