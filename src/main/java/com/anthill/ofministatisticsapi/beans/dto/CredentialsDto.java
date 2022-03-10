package com.anthill.ofministatisticsapi.beans.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CredentialsDto {

    private String login, password;
    private long telegramId;
}
