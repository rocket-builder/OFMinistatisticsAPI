package com.anthill.ofministatisticsapi.services;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelItemDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.ScrapperStatisticDto;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class DataScrapperService {

    @Value("${ofministatistics.app.url}")
    private String baseUrl;

    public Statistic getStatistic(String url)
            throws CannotGetStatisticException, ResourceNotFoundedException {
        var dto = getStatisticsDto(url);
        return dtoToStatistic(dto);
    }

    public OnlyFansModelItemDto getModelWithStatistic(String url)
            throws CannotGetStatisticException, ResourceNotFoundedException {
        var dto = getStatisticsDto(url);

        var statistic = dtoToStatistic(dto);

        var model = OnlyFansModel.builder()
                .avatarUrl(dto.getAvatarUrl())
                .name(dto.getName())
                .url(url)
                .userAssoc(new ArrayList<>())
                .build();

        return OnlyFansModelItemDto.builder()
                .model(model)
                .statistic(statistic).build();
    }

    public ScrapperStatisticDto getStatisticsDto(String url)
            throws CannotGetStatisticException, ResourceNotFoundedException {

        try(var httpClient = new DefaultHttpClient()){
            var requestUrl = baseUrl + "/ModelStatistics?url=" + url;
            var request = new HttpGet(requestUrl);

            request.addHeader("accept", "application/json");

            var response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 404){
                throw new ResourceNotFoundedException(url);
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            var json = EntityUtils.toString(response.getEntity());

            return new ObjectMapper().readValue(json, ScrapperStatisticDto.class);
        }
        catch (IOException ex){
            ex.printStackTrace();
            throw new CannotGetStatisticException();
        }
    }

    private Statistic dtoToStatistic(ScrapperStatisticDto dto){
        return Statistic.builder()
                .subscribersCount(dto.getSubscribersCount())
                .likesCount(dto.getLikesCount())
                .photosCount(dto.getPhotosCount())
                .videosCount(dto.getVideosCount())
                .moment(dto.getMoment())
                .build();
    }
}
