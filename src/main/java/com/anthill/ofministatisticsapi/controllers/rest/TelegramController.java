package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import com.anthill.ofministatisticsapi.exceptions.CannotCheckExistsChatException;
import com.anthill.ofministatisticsapi.exceptions.UserNotFoundedException;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.services.MessageGeneratorService;
import com.anthill.ofministatisticsapi.services.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Telegram")
@RequestMapping("/telegram")
@RestController
public class TelegramController {

    private final UserRepos userRepos;
    private final TelegramService telegramService;
    private final MessageGeneratorService messageGeneratorService;

    public TelegramController(UserRepos userRepos, TelegramService telegramService,
                              MessageGeneratorService messageGeneratorService) {
        this.userRepos = userRepos;
        this.telegramService = telegramService;
        this.messageGeneratorService = messageGeneratorService;
    }

    @PostMapping("/send/{telegramId}/OnlyFansModelAdded")
    public TelegramMessageDto sendApprove(@PathVariable("telegramId") long telegramId, @RequestBody OnlyFansModel model)
            throws UserNotFoundedException {
        var user = userRepos.findByTelegramId(telegramId)
                .orElseThrow(UserNotFoundedException::new);

        var message = messageGeneratorService
                .getOnlyFansModelAdded(user.getTelegramId(), model);

        telegramService.sendMessage(message);
        return message;
    }

    @GetMapping("/chat/{telegramId}/exists")
    public boolean isChatExists(@PathVariable("telegramId") long telegramId)
            throws CannotCheckExistsChatException {

        return telegramService.isChatExists(telegramId);
    }
}
