package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
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
    private final TelegramService telegramService;

    public DataUpdaterService(StatisticRepos statisticRepos, OnlyFansModelRepos modelRepos,
                              DataScrapperService scrapperService, TelegramService telegramService) {
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
                var dto = scrapperService.getModelWithStatistic(model.getUrl());
                var modelUpdate = dto.getModel();
                var statisticUpdate = dto.getStatistic();

                statisticUpdate.setModel(model);
                statisticUpdate.setGlobalPoint(true);

                statisticRepos.save(statisticUpdate);

                model.setAvatarUrl(modelUpdate.getAvatarUrl());
                model.setName(modelUpdate.getName());
                modelRepos.save(model);

                log.info("Global point statistic for " + model.getName() + " successfully saved!");
            } catch (CannotGetStatisticException | RuntimeException | ResourceNotFoundedException ex){
                log.error("Cannot update global statistic for " + model.getName() + " because " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3600000)
    public void updateAllModelsStatistics() {
        log.info("start update all models statistics at "+ LocalDateTime.now());

        var models = modelRepos.findAll();

        models.forEach(model -> {
            try {
                var update = scrapperService.getStatistic(model.getUrl());
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
                    model.getAlertUsers().forEach(user ->
                            telegramService.sendUpdate(
                                    new TelegramUpdateDto(user.getTelegramId(), model, difference)));

                    statisticRepos.save(update);
                    log.info(model.getName() + " statistic successfully updated!");
                } else {
                    log.info(model.getName() + "statistic has no difference");
                }
            }
            catch (CannotGetStatisticException ex){
                ex.printStackTrace();

                model.getUsers().forEach(user -> {
                    var message = TelegramMessageDto.builder()
                            .message("Cannot get data for model " + model.getName() + " :(")
                            .telegramId(user.getTelegramId())
                            .build();
                    telegramService.sendMessage(message);
                });
            }
            catch (ResourceNotFoundedException ex){
                ex.printStackTrace();

                model.getUsers().forEach(user -> {
                    var message = TelegramMessageDto.builder()
                            .message("Model " + model.getName() + " already not exists on onlyfans :(")
                            .telegramId(user.getTelegramId())
                            .build();
                    telegramService.sendMessage(message);
                });
            }
            catch (Exception ex){
                ex.printStackTrace();

                model.getUsers().forEach(user -> {
                    var message = TelegramMessageDto.builder()
                            .message("Something awful happened while processing the model " + model.getName() + " :(")
                            .telegramId(user.getTelegramId())
                            .build();
                    telegramService.sendMessage(message);
                });
            }
        });
        
        log.info("All models updated");
    }
}
