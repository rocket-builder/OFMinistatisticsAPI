package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@Tag(name = "OnlyFansModel")
@RequestMapping("/onlyFansModel")
@RestController
public class OnlyFansModelController extends AbstractController<OnlyFansModel, OnlyFansModelRepos> {

    private final DataScrapperService scrapperService;

    protected OnlyFansModelController(OnlyFansModelRepos repos, DataScrapperService scrapperService) {
        super(repos);
        this.scrapperService = scrapperService;
    }

    @GetMapping("/{id}/statistics/now")
    public Statistic getLastStatistics(@PathVariable("id") long id)
            throws ResourceNotFoundedException, IOException, URISyntaxException {
        var model = repos.findById(id);

        if(model.isEmpty()){
            throw new ResourceNotFoundedException();
        }

        return scrapperService.getStatistics(model.get().getUrl());
    }
}
