package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class TelegramUpdateService {

    @Value("${ofministatistics.tg.bot.alert.url}")
    private String alertUrl;

    @Value("${ofministatistics.tg.bot.message.url}")
    private String messageUrl;

    public void sendUpdate(TelegramUpdateDto update) {
        try {
            var response = sendPost(alertUrl, update);

            if (response.getStatusLine().getStatusCode() == 200){
                log.info("Successful send telegram update to id " + update.getTelegramId());
            } else {
                log.info(response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception ex){
            log.info("Cannot send telegram update to id" + update.getTelegramId() + " :(");
            log.error(ex.getMessage());
        }
    }

    public void sendMessage(TelegramMessageDto message){
        try{
            var response = sendPost(messageUrl, message);

            if (response.getStatusLine().getStatusCode() == 200){
                log.info("Successful send telegram message to id " + message.getTelegramId());
            } else {
                log.info(response.getStatusLine().getReasonPhrase());
            }
        }
        catch (Exception ex){
            log.info("Cannot send telegram message to id" + message.getTelegramId() + " :(");
            log.error(ex.getMessage());
        }
    }

    public CloseableHttpResponse sendPost(String url, Object object) throws IOException {
            var httpClient = new DefaultHttpClient();

            var json = new ObjectMapper().writeValueAsString(object);

            var request = new HttpPost(url);

            request.setEntity(new StringEntity(json));
            request.setHeader("Content-Type", "application/json");

            var response = httpClient.execute(request);
            httpClient.close();

            return response;
    }
}
