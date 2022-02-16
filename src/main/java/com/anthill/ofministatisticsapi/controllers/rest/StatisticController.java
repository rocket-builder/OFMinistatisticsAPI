package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.repos.StatisticRepos;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Statistic")
@RequestMapping("/statistic")
@RestController
public class StatisticController extends AbstractController<Statistic, StatisticRepos> {

    protected StatisticController(StatisticRepos repos) {
        super(repos);
    }

}
