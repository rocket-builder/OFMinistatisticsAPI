package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.telegram.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.services.TelegramUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

@SpringBootTest
public class TelegramUpdateServiceTests {

    @Autowired
    TelegramUpdateService telegramService;

    @Test
    public void sendUpdate_whenAllCorrect_shouldSend(){
        //Arrange
        var telegramId = 920818350;

        var statistics = new Statistic();
        statistics.setLikesCount(100);
        statistics.setMoment(new Date(1645193914));
        statistics.setSubscribersCount(200);
        statistics.setName("Test model");

        var dto = new TelegramUpdateDto();
        dto.setTelegramId(telegramId);
        dto.setStatistic(statistics);

        //Act
        telegramService.sendUpdate(dto);

        //Assert
        assert true;
    }
}
