package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.CannotGetStatisticException;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Statistic")
@RequestMapping("/statistic")
@RestController
public class StatisticController extends AbstractController<Statistic, StatisticRepos> {

    private final DataScrapperService scrapperService;

    protected StatisticController(StatisticRepos repos, DataScrapperService scrapperService) {
        super(repos);
        this.scrapperService = scrapperService;
    }

    @GetMapping("/now")
    public Statistic getStatisticsNow(@RequestParam String url) throws CannotGetStatisticException {

        return scrapperService.getStatistics(url);
    }
}
