package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelCalculatedDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CalculatedStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.enums.DateUnit;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "OnlyFansModel")
@RequestMapping("/onlyFansModel")
@RestController
public class OnlyFansModelController extends AbstractController<OnlyFansModel, OnlyFansModelRepos> {

    private final DataScrapperService scrapperService;
    private final CurrentStatisticService statisticService;

    protected OnlyFansModelController(OnlyFansModelRepos repos, DataScrapperService scrapperService,
                                      CurrentStatisticService statisticService) {
        super(repos);
        this.scrapperService = scrapperService;
        this.statisticService = statisticService;
    }

    @GetMapping("/search/filter")
    public OnlyFansModelCalculatedDto searchModel(@RequestParam String url,
                                                  @RequestParam(defaultValue = "MONTH") DateUnit unit,
                                                  @RequestParam(defaultValue = "6") int count)
            throws CannotGetStatisticException, ResourceNotFoundedException {
        var modelOptional = repos.findByUrl(url);

        OnlyFansModelCalculatedDto dto;
        if (modelOptional.isPresent()){
            dto = statisticService.getCalculatedStatistic(modelOptional.get(), unit, count);
        } else {
            var modelWithStatistic = scrapperService.getModelWithStatistic(url);
            dto = OnlyFansModelCalculatedDto.builder()
                    .model(modelWithStatistic.getModel())
                    .current(modelWithStatistic.getStatistic())
                    .calculatedStatistics(new ArrayList<>())
                    .build();
        }

        return dto;
    }

    @GetMapping("/{id}/statistics/filter")
    public List<CalculatedStatisticDto> getStatisticsRange(@PathVariable("id") long id,
                                                           @RequestParam(defaultValue = "MONTH") DateUnit unit,
                                                           @RequestParam(defaultValue = "6") int count)
            throws ResourceNotFoundedException {
        var model = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        return statisticService.getCalculatedData(model, unit, count);
    }
}
