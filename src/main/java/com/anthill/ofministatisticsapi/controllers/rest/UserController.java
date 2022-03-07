package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.beans.dto.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.*;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.anthill.ofministatisticsapi.security.MD5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractController<User, UserRepos> {

    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;
    private final CurrentStatisticService currentStatisticService;

    protected UserController(UserRepos repos, OnlyFansModelRepos modelRepos,
                             DataScrapperService scrapperService, CurrentStatisticService currentStatisticService) {
        super(repos);
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
        this.currentStatisticService = currentStatisticService;
    }

    @GetMapping("/login/{login}")
    public User getUserByLogin(@PathVariable("login") String login) throws UserNotFoundedException {
        var user = repos.findByLogin(login);

        if (user.isEmpty()){
            throw new UserNotFoundedException();
        }

        return user.get();
    }

    @GetMapping("/{login}/statistic")
    public List<CurrentStatisticDto> getCurrentStatistic(@PathVariable("login") String login) throws UserNotFoundedException {
        var user = repos.findByLogin(login);

        if (user.isEmpty()){
            throw new UserNotFoundedException();
        }

        List<CurrentStatisticDto> statisticDtos = new ArrayList<>();
        user.get().getModels().forEach(model ->
        {
            var stats = currentStatisticService.updateByModel(model);
            statisticDtos.add(stats);
        });

        return statisticDtos;
    }

    @PostMapping("/login")
    public User login(@RequestBody User auth) throws UserNotFoundedException, IncorrectPasswordException {
        var user = repos.findByLogin(auth.getLogin());

        if (user.isEmpty()){
            throw new UserNotFoundedException();
        }
        if(!user.get().getPassword().equals(MD5.getHash(auth.getPassword()))){
            throw new IncorrectPasswordException();
        }

        return user.get();
    }

    @PostMapping("/signUp")
    public User signUp(@RequestBody User signUp) throws LoginAlreadyTakenException {
        var user = repos.findByLogin(signUp.getLogin());

        if(user.isPresent()){
            throw new LoginAlreadyTakenException();
        }
        var password = MD5.getHash(signUp.getPassword());
        signUp.setPassword(password);

        return repos.save(signUp);
    }

    @PostMapping("/{login}/model")
    public OnlyFansModel addModel(@PathVariable("login") String login, String url)
            throws UserNotFoundedException, ResourceAlreadyExists, CannotGetStatisticException {

        var user = repos.findByLogin(login);
        if(user.isEmpty()){
            throw new UserNotFoundedException();
        }

        var modelExists = user.get().getModels()
                .stream()
                .anyMatch(model -> model.getUrl().equals(url));
        if(modelExists){
            throw new ResourceAlreadyExists();
        }

        var statistics = scrapperService.getStatistics(url);

        var model = new OnlyFansModel(
                statistics.getName(), url, user.get(), List.of(statistics));

        return modelRepos.save(model);
    }

    @GetMapping("{login}/models")
    public List<OnlyFansModel> getUserModels(@PathVariable("login") String login) throws UserNotFoundedException {

        var user = repos.findByLogin(login);
        if(user.isEmpty()){
            throw new UserNotFoundedException();
        }

        return user.get().getModels();
    }
}
