package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.beans.dto.CredentialsDto;
import com.anthill.ofministatisticsapi.beans.dto.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.OnlyFansModelStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.*;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.anthill.ofministatisticsapi.security.MD5;

import java.util.Calendar;
import java.util.Date;
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

    @GetMapping("/telegramId/{telegramId}")
    public User getUserByLogin(@PathVariable("telegramId") long telegramId) throws UserNotFoundedException {

        return repos.findByTelegramId(telegramId)
                .orElseThrow(UserNotFoundedException::new);
    }

    @GetMapping("/{telegramId}/statistic")
    public List<CurrentStatisticDto> getCurrentStatistic(@PathVariable("telegramId") long telegramId) throws UserNotFoundedException {
        var user = repos.findByTelegramId(telegramId)
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
        var user = repos.findFirstByLoginOrTelegramId(
                signUp.getLogin(), signUp.getTelegramId());

        if(user.isPresent()){
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

    @PutMapping("/{id}/credentials")
    public User updateUserCredentials(@PathVariable("id") long id, @RequestBody CredentialsDto credentials)
            throws UserNotFoundedException {
        var user = repos.findById(id)
                .orElseThrow(UserNotFoundedException::new);

        if(credentials.getLogin() != null){
            user.setLogin(credentials.getLogin());
        }
        if(credentials.getPassword() != null){
            var hash = MD5.getHash(credentials.getPassword());
            user.setPassword(hash);
        }
        if(credentials.getTelegramId() > 0){
            user.setTelegramId(credentials.getTelegramId());
        }

        return repos.save(user);
    }

    @GetMapping("/{telegramId}/models")
    public List<OnlyFansModel> getUserModels(@PathVariable("telegramId") long telegramId) throws UserNotFoundedException {
        var user = repos.findByTelegramId(telegramId)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModels();
    }

    @GetMapping("/{id}/modelsStatistic")
    public List<OnlyFansModelStatisticDto> getModelsStatistic(@PathVariable("id") long id,
                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start)
            throws UserNotFoundedException {
        var user = repos.findById(id)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModels().stream().map(model -> {
            var statistic = model.getStatistics().stream()
                    .filter(s -> s.getMoment().after(start))
                    .collect(Collectors.toList());
            var last = statistic.size() > 0? statistic.get(statistic.size() - 1) : null;

            return OnlyFansModelStatisticDto.builder()
                    .model(model)
                    .historical(statistic)
                    .current(last)
                    .build();
        }).collect(Collectors.toList());
    }
}
