package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class TelegramUpdateService {

    @Value("${ofministatistics.tg.bot.alert.url}")
    private String requestUrl;

    public void sendUpdate(TelegramUpdateDto update) {
        try (var httpClient = new DefaultHttpClient()){
            var json = new ObjectMapper().writeValueAsString(update);

            var request = new HttpPost(requestUrl);

            request.setEntity(new StringEntity(json));
            request.setHeader("Content-Type", "application/json");

            var response = httpClient.execute(request);
            log.info(response.getStatusLine().getReasonPhrase());

        } catch (Exception ex){
            log.info("Cannot send telegram update to id" + update.getTelegramId() + " :(");
            log.error(ex.getMessage());
        }
    }
}
