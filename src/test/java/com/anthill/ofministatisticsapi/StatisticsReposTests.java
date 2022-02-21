package com.anthill.ofministatisticsapi;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StatisticsReposTests {

    @Autowired
    private StatisticRepos statisticRepos;
    @Autowired
    private OnlyFansModelRepos modelRepos;

    @Test
    public void selectLastStats_whenAllCorrect_shouldSelect(){
        //Arrange

        var modelId = 274;
        var model = modelRepos.findById(modelId);
        //Act
        var stats = statisticRepos.findLastByModel(model.get().getId());

        //Assert
        assert stats != null;
    }
}
