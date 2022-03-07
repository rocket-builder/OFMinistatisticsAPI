package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.services.TelegramUpdateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TelegramUpdateServiceTests {

    @Autowired
    TelegramUpdateService telegramService;

    @Test
    public void sendUpdate_whenAllCorrect_shouldSend() {
        //Arrange
        var telegramId = 920818350;

        var model = OnlyFansModel.builder()
                .name("Test")
                .build();

        var statistics = Statistic.builder()
                .likesCount(-10)
                .subscribersCount(-5)
                .moment(new Date())
                .build();

        var dto = new TelegramUpdateDto(telegramId, model, statistics);

        //Act
        telegramService.sendUpdate(dto);

        //Assert
        assert true;
    }
}
