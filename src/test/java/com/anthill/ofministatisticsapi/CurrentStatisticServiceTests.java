package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CurrentStatisticServiceTests {

    @Autowired
    private CurrentStatisticService statisticService;

    @Test
    public void getCurrentStatistic_whenAllCorrect_shouldGet(){
        //Arrange
        var model = new OnlyFansModel();
        model.setId(648);

        //Act
        var statistic = statisticService.updateByModel(model);

        //Assert
        assert statistic != null;
    }
}
