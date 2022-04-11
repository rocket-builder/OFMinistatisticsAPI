package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.PasswordResetToken;
import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import org.springframework.stereotype.Service;

@Service
public class MessageGeneratorService {

    private final UrlGeneratorService urlGeneratorService;

    public MessageGeneratorService(UrlGeneratorService urlGeneratorService) {
        this.urlGeneratorService = urlGeneratorService;
    }

    public TelegramMessageDto getOnlyFansModelAdded(long telegramId, OnlyFansModel model){
        var message = "Model " + model.getName() + " successfully added!";

        return TelegramMessageDto.builder()
                .message(message)
                .telegramId(telegramId)
                .build();
    }

    public TelegramMessageDto getPasswordReset(long telegramId, PasswordResetToken token){
        var message = "Hello, your password reset link: " + urlGeneratorService.getPasswordResetUrl(token);

        return TelegramMessageDto.builder()
                .message(message)
                .telegramId(telegramId)
                .build();
    }
}
