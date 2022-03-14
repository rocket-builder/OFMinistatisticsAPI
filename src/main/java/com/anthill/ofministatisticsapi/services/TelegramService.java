package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.dto.TelegramMessageDto;
import com.anthill.ofministatisticsapi.beans.dto.TelegramUpdateDto;
import com.anthill.ofministatisticsapi.exceptions.CannotCheckExistsChatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TelegramService {

    @Value("${ofministatistics.tg.bot.url}")
    private String botUrl;

    public void sendUpdate(TelegramUpdateDto update) {
        try {
            var response = sendPost(botUrl + "/api/message/alert", update);

            if (response.getStatusLine().getStatusCode() == 200){
                log.info("Successful send telegram update to id " + update.getTelegramId());
            } else {
                log.info("Cannot send telegram update to id " + update.getTelegramId() + " :(");
                log.info(response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception ex){
            log.info("Cannot send telegram update to id " + update.getTelegramId() + " :(");
            log.error(ex.getMessage());
        }
    }

    public void sendMessage(TelegramMessageDto message){
        try{
            var response = sendPost(botUrl + "/api/message/send", message);

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

    public boolean isChatExists(long telegramId) throws CannotCheckExistsChatException {
        try(var httpClient = new DefaultHttpClient()) {
            var request = new HttpGet(botUrl + "/isChatExists?telegramId=" + telegramId);
            var response = httpClient.execute(request);

            var result = new String(
                    response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

            return Boolean.parseBoolean(result);
        } catch (Exception ex){
            log.error(ex.getMessage());

            throw new CannotCheckExistsChatException();
        }
    }

    private CloseableHttpResponse sendPost(String url, Object object) throws IOException {
        var httpClient = new DefaultHttpClient();

        var json = new ObjectMapper().writeValueAsString(object);

        var request = new HttpPost(url);

        request.setEntity(new StringEntity(json));
        request.setHeader("Content-Type", "application/json; charset=utf-8");

        var response = httpClient.execute(request);
        httpClient.close();

        return response;
    }
}
