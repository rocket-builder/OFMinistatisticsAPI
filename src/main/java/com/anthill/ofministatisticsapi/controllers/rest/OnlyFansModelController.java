package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelGraphicDto;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelItemDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.enums.DateUnit;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "OnlyFansModel")
@RequestMapping("/onlyFansModel")
@RestController
public class OnlyFansModelController extends AbstractController<OnlyFansModel, OnlyFansModelRepos> {

    private final CurrentStatisticService statisticService;

    protected OnlyFansModelController(OnlyFansModelRepos repos, CurrentStatisticService statisticService) {
        super(repos);
        this.statisticService = statisticService;
    }

    @GetMapping("/search/filter")
    public OnlyFansModelGraphicDto searchModel(@RequestParam String url,
                                               @RequestParam(defaultValue = "MONTH") DateUnit unit,
                                               @RequestParam(defaultValue = "6") int count)
            throws CannotGetStatisticException, ResourceNotFoundedException {
        var model = repos.findOldestByUrl(url)
                .orElseThrow(ResourceNotFoundedException::new);

        return statisticService.getWithGraphic(model, unit, count);
    }

    @GetMapping("/{id}/statistics/filter")
    public List<Statistic> getStatisticsRange(@PathVariable("id") long id,
                                              @RequestParam(defaultValue = "MONTH") DateUnit unit,
                                              @RequestParam(defaultValue = "6") int count)
            throws ResourceNotFoundedException {
        var model = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        return statisticService.getGraphicData(model, unit, count);
    }

    @PutMapping("/{id}/alerts")
    public OnlyFansModel setModelAlerts(@PathVariable("id") long id, @RequestParam boolean enable)
            throws ResourceNotFoundedException {
        var model = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        model.setNeedAlerts(enable);
        return repos.save(model);
    }
}
