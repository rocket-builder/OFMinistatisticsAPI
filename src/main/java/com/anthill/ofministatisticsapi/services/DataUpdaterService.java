package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class DataUpdaterService {

    private final StatisticRepos statisticRepos;
    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;

    public DataUpdaterService(StatisticRepos statisticRepos, OnlyFansModelRepos modelRepos,
                              DataScrapperService scrapperService) {
        this.statisticRepos = statisticRepos;
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
    }

    @Scheduled(fixedDelay = 86400)
    public void updateAllModelsStatistics() {
        log.info("start update all models statistics at "+ LocalDateTime.now());

        var models = modelRepos.findAll();
        models.forEach(model -> {
            try{
                var update = scrapperService.getStatistics(model.getUrl());
                update.setModel(model);

                statisticRepos.save(update);

                log.info(model.getName() + " statistic successfully updated!");
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
