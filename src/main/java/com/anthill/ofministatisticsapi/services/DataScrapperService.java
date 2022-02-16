package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class DataScrapperService {

    @Value("${ofministatistics.app.url}")
    private String baseUrl;

    private final DefaultHttpClient httpClient = new DefaultHttpClient();

    public Statistic getStatistics(String url)
            throws IOException, URISyntaxException {

        var requestUrl = baseUrl + "/ModelStatistics?url=" + url;
        var request = new HttpGet(requestUrl);

        request.addHeader("accept", "application/json");

        var response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }

        var json = EntityUtils.toString(response.getEntity());

        return new ObjectMapper().readValue(json, Statistic.class);
    }
}
