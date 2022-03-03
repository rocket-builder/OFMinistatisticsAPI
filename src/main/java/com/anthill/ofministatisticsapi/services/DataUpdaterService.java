package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Scheduled(cron = "0 0 0 * * *")
    public void updateAllModelsGlobalPoint(){
        log.info("Start receive 00:00 statistic");

        modelRepos.findAll().forEach(model -> {
            try{
                var update = scrapperService.getStatistics(model.getUrl());
                update.setModel(model);
                update.setGlobalPoint(true);

                statisticRepos.save(update);
                log.info("Global point statistic for " + model.getName() + " successfully saved!");
            } catch (IOException | RuntimeException ex){
                log.error("Cannot update global statistic for " + model.getName());
                ex.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3600000)
    public void updateAllModelsStatistics() {
        log.info("start update all models statistics at "+ LocalDateTime.now());

        var models = modelRepos.findAll();
        models.forEach(model -> {
            try{
                var update = scrapperService.getStatistics(model.getUrl());
                update.setModel(model);

                var lastGlobalPointOptional = statisticRepos.findLastGlobalPointByModel(model.getId());
                var difference = lastGlobalPointOptional
                        .map(lastGlobalPoint -> Statistic.subtract(update, lastGlobalPoint))
                        .orElseGet(() -> {
                            var todayFirstOptional = statisticRepos.findTodayFirstByModel(model.getId());
                            return todayFirstOptional
                                    .map(todayFirst -> Statistic.subtract(update, todayFirst))
                                    .orElseGet(() -> {
                                        statisticRepos.save(update);
                                        return new Statistic();
                                    });
                        });

                if(!difference.isZero()){
                    telegramService.sendUpdate(
                            new TelegramUpdateDto(model.getUser().getTelegramId(), difference));

                    statisticRepos.save(update);
                    log.info(model.getName() + " statistic successfully updated!");
                } else {
                    log.info(model.getName() + "statistic has no difference");
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
