package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.PasswordResetToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlGeneratorService {

    @Value("${ofministatistics.password.reset.url}")
    private String passwordResetUrl;


    public String getPasswordResetUrl(PasswordResetToken token){
        return passwordResetUrl + "?token=" + token.getToken();
    }
}
