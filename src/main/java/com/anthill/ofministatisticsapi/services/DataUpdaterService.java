package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.telegram.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class DataUpdaterService {

    private final StatisticRepos statisticRepos;
    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;
    private final TelegramUpdateService telegramService;

    public DataUpdaterService(StatisticRepos statisticRepos, OnlyFansModelRepos modelRepos,
                              DataScrapperService scrapperService, TelegramUpdateService telegramService) {
        this.statisticRepos = statisticRepos;
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
        this.telegramService = telegramService;
    }

    @Scheduled(fixedDelay = 3600000)
    public void updateAllModelsStatistics() {
        log.info("start update all models statistics at "+ LocalDateTime.now());

        var models = modelRepos.findAll();
        models.forEach(model -> {
            try{
                var update = scrapperService.getStatistics(model.getUrl());
                update.setModel(model);

                var todayFirst = statisticRepos.findTodayFirstByModel(model.getId());

                todayFirst.ifPresentOrElse(today -> {
                    var difference = Statistic.subtract(update, today);

                    if(!difference.isZero()){
                        telegramService.sendUpdate(
                                new TelegramUpdateDto(model.getUser().getTelegramId(), difference));

                        statisticRepos.save(update);
                        log.info(model.getName() + " statistic successfully updated!");
                    } else {
                        log.info(model.getName() + "statistic has no difference");
                    }
                }, () -> statisticRepos.save(update));

            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
