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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractController<User, UserRepos> {

    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;
    private final CurrentStatisticService currentStatisticService;

    protected UserController(UserRepos repos, OnlyFansModelRepos modelRepos,
                             DataScrapperService scrapperService,
                             CurrentStatisticService currentStatisticService) {
        super(repos);
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
        this.currentStatisticService = currentStatisticService;
    }

    @GetMapping("/login/{login}")
    public User getUserByLogin(@PathVariable("login") String login) throws UserNotFoundedException {

        return repos.findByLogin(login)
                .orElseThrow(UserNotFoundedException::new);
    }

    @GetMapping("/{login}/statistic")
    public List<CurrentStatisticDto> getCurrentStatistic(@PathVariable("login") String login) throws UserNotFoundedException {
        var user = repos.findByLogin(login)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModels().stream()
                .map(currentStatisticService::updateByModel)
                .collect(Collectors.toList());
    }

    @PostMapping("/login")
    public User login(@RequestBody User auth) throws UserNotFoundedException, IncorrectPasswordException {
        var user = repos.findByLogin(auth.getLogin())
                .orElseThrow(UserNotFoundedException::new);

        if(!user.getPassword().equals(MD5.getHash(auth.getPassword()))){
            throw new IncorrectPasswordException();
        }

        return user;
    }

    @PostMapping("/signUp")
    public User signUp(@RequestBody User signUp) throws UserAlreadyExistsException {
        var isUserExists = repos.existsByLoginOrTelegramId(
                signUp.getLogin(), signUp.getTelegramId());

        if(isUserExists){
            throw new UserAlreadyExistsException();
        }
        var password = MD5.getHash(signUp.getPassword());
        signUp.setPassword(password);

        return repos.save(signUp);
    }

    @PostMapping("/signOut")
    public Object signOut(){
        return null;
    }

    @PostMapping("/{login}/model")
    public OnlyFansModel addModel(@PathVariable("login") String login, String url)
            throws UserNotFoundedException, ResourceAlreadyExists, CannotGetStatisticException {
        var user = repos.findByLogin(login)
                .orElseThrow(UserNotFoundedException::new);

        var modelExists = user.getModels()
                .stream()
                .anyMatch(model -> model.getUrl().equals(url));
        if(modelExists){
            throw new ResourceAlreadyExists();
        }

        var dto = scrapperService.getModelWithStatistic(url);

        var model = dto.getModel();
        model.setUser(user);
        model.setStatistics(List.of(dto.getStatistic()));

        return modelRepos.save(model);
    }

    @GetMapping("{login}/models")
    public List<OnlyFansModel> getUserModels(@PathVariable("login") String login) throws UserNotFoundedException {
        var user = repos.findByLogin(login)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModels();
    }
}
