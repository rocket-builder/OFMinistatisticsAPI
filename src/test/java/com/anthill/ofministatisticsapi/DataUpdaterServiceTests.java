package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.services.DataUpdaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataUpdaterServiceTests {

    @Autowired
    private DataUpdaterService updaterService;

    @Test
    public void sendUpdates_whenAllCorrect_shouldSend(){
        //Arrange
        updaterService.updateAllModelsStatistics();

        //Act

        //Assert
    }

    @Test
    public void saveGlobalPointUpdates_whenAllCorrect_shouldSave(){
        //Arrange
        updaterService.updateAllModelsGlobalPoint();

        //Act

        //Assert

    }
}
