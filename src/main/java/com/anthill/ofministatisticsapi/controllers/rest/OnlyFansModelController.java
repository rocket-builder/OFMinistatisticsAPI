package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.dto.OnlyFansModelWithStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Tag(name = "OnlyFansModel")
@RequestMapping("/onlyFansModel")
@RestController
public class OnlyFansModelController extends AbstractController<OnlyFansModel, OnlyFansModelRepos> {

    private final StatisticRepos statisticRepos;
    private final DataScrapperService scrapperService;

    protected OnlyFansModelController(OnlyFansModelRepos repos, StatisticRepos statisticRepos,
                                      DataScrapperService scrapperService) {
        super(repos);
        this.statisticRepos = statisticRepos;
        this.scrapperService = scrapperService;
    }

    @GetMapping("/{id}/statistics/now")
    public Statistic getLastStatistics(@PathVariable("id") long id)
            throws ResourceNotFoundedException, CannotGetStatisticException {
        var model = repos.findById(id);

        if(model.isEmpty()){
            throw new ResourceNotFoundedException();
        }

        return scrapperService.getStatistic(model.get().getUrl());
    }

    @GetMapping("/search")
    public OnlyFansModelWithStatisticDto searchModel(@RequestParam String url)
            throws CannotGetStatisticException {

        return scrapperService.getModelWithStatistic(url);
    }

    @GetMapping("/{id}/statistics/range")
    public List<Statistic> getStatisticsRange(@PathVariable("id") long id, @RequestParam Date start, @RequestParam Date end)
            throws ResourceNotFoundedException {
        var model = repos.findById(id);

        if(model.isEmpty()){
            throw new ResourceNotFoundedException();
        }

        return statisticRepos.findAllByRange(start, end, id);
    }
}
