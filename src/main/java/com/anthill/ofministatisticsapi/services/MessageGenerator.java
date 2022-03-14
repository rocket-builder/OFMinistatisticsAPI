package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import org.springframework.stereotype.Service;

@Service
public class MessageGenerator {

    public TelegramMessageDto GetOnlyFansModelAdded(long telegramId, OnlyFansModel model){
        var message = "Model " + model.getName() + " successfully added!";

        return TelegramMessageDto.builder()
                .message(message)
                .telegramId(telegramId)
                .build();
    }
}
