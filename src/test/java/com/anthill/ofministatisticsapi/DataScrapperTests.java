package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataScrapperTests {

    @Autowired
    DataScrapperService scrapperService;

    @Test
    public void getData_whenAllCorrect_shouldGet()
            throws CannotGetStatisticException {
        //Arrange
        var url = "https://onlyfans.com/katie_tasty";

        //Act
        var statistics = scrapperService.getStatisticsDto(url);

        //Assert
        assert statistics.getName().length() > 0;
    }
}
