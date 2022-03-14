package com.anthill.ofministatisticsapi.beans.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TelegramMessageDto {

    private long telegramId;
    private String message;
}
