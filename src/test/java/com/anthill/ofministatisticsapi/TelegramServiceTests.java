package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.exceptions.CannotCheckExistsChatException;
import com.anthill.ofministatisticsapi.services.TelegramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TelegramServiceTests {

    @Autowired
    TelegramService telegramService;

    @Test
    public void sendUpdate_whenAllCorrect_shouldSend() {
        //Arrange
        var telegramId = 713392247;//920818350;

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

    @Test
    public void sendMessage_whenAllCorrect_shouldSend() {
        //Arrange
        var telegramId = 920818350;
        var text = "Hello";
        var message = TelegramMessageDto.builder()
                .message(text)
                .telegramId(telegramId)
                .build();

        //Act
        telegramService.sendMessage(message);

        //Assert
        assert true;
    }

    @Test
    public void isChatExists_whenAllCorrect_shouldExists() throws CannotCheckExistsChatException {
        //Arrange
        var telegramId = 920818350;

        //Act
        var exists = telegramService.isChatExists(telegramId);

        //Assert
        assert exists;
    }
}
