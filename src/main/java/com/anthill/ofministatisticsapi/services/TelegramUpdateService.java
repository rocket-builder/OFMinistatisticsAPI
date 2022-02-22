package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.telegram.TelegramUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramUpdateService {

    @Value("${ofministatistics.tg.bot.url}")
    private String baseUrl;

    public void sendUpdate(TelegramUpdateDto update) {
        try (var httpClient = new DefaultHttpClient()){
            var requestUrl = baseUrl + "/updater.php";
            var json = new ObjectMapper().writeValueAsString(update);

            var request = new HttpPost(requestUrl);

            request.addHeader("accept", "application/json");
            request.setEntity(new StringEntity(json));

            var response = httpClient.execute(request);
            log.info(response.getStatusLine().getReasonPhrase());

        } catch (Exception ex){
            log.info("Cannot send telegram update to id" + update.getTelegramId() + " :(");
            log.error(ex.getMessage());
        }
    }
}
