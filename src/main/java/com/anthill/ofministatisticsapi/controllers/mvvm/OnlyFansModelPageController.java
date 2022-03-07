package com.anthill.ofministatisticsapi.controllers.mvvm;

import com.anthill.ofministatisticsapi.beans.dto.OnlyFansModelStatisticDto;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OnlyFansModelPage")
@RequestMapping("/page/onlyFansModel")
@RestController
public class OnlyFansModelPageController {

    private final OnlyFansModelRepos modelRepos;
    private final StatisticRepos statisticRepos;
    private final DataScrapperService scrapperService;

    public OnlyFansModelPageController(OnlyFansModelRepos modelRepos,
                                       StatisticRepos statisticRepos, DataScrapperService scrapperService) {
        this.modelRepos = modelRepos;
        this.statisticRepos = statisticRepos;
        this.scrapperService = scrapperService;
    }

    @GetMapping("/{id}/statistic")
    public OnlyFansModelStatisticDto getLastDays(@PathVariable("id") long id, @RequestParam int days)
            throws ResourceNotFoundedException, CannotGetStatisticException {
        var modelOptional = modelRepos.findById(id);

        if(modelOptional.isEmpty()){
            throw new ResourceNotFoundedException();
        }
        var model = modelOptional.get();
        var current = scrapperService.getStatistic(modelOptional.get().getUrl());
        var historical = statisticRepos.findLastGlobalPointsByModel(id, days);

        return new OnlyFansModelStatisticDto(model, current, historical);
    }
}
