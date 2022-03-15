package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelCalculatedStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CalculatedStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CredentialsDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.*;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import com.anthill.ofministatisticsapi.services.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.util.streamex.StreamEx;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.anthill.ofministatisticsapi.security.MD5;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractController<User, UserRepos> {

    private final TelegramService telegramService;
    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;
    private final CurrentStatisticService currentStatisticService;

    protected UserController(UserRepos repos, TelegramService telegramService, OnlyFansModelRepos modelRepos,
                             DataScrapperService scrapperService,
                             CurrentStatisticService currentStatisticService) {
        super(repos);
        this.telegramService = telegramService;
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
    public User signUp(@RequestBody User signUp)
            throws UserAlreadyExistsException, CannotCheckExistsChatException, TelegramChatNotExists {
        var user = repos.findFirstByLoginOrTelegramId(
                signUp.getLogin(), signUp.getTelegramId());

        if(user.isPresent()){
            throw new UserAlreadyExistsException();
        }
        if(!telegramService.isChatExists(signUp.getTelegramId())){
            throw new TelegramChatNotExists();
        }

        var password = MD5.getHash(signUp.getPassword());
        signUp.setPassword(password);

        return repos.save(signUp);
    }

    @PostMapping("/signOut")
    public Object signOut(){
        return null;
    }

    @PostMapping("/{telegramId}/model")
    public OnlyFansModel addModel(@PathVariable("telegramId") long telegramId, String url)
            throws UserNotFoundedException, ResourceAlreadyExists, CannotGetStatisticException {
        var user = repos.findByTelegramId(telegramId)
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
        model.setNeedAlerts(true);

        return modelRepos.save(model);
    }

    @PutMapping("/{id}/credentials")
    public User updateUserCredentials(@PathVariable("id") long id, @RequestBody CredentialsDto credentials)
            throws UserNotFoundedException, TelegramChatNotExists, CannotCheckExistsChatException {
        var user = repos.findById(id)
                .orElseThrow(UserNotFoundedException::new);

        if(!telegramService.isChatExists(credentials.getTelegramId())){
            throw new TelegramChatNotExists();
        }
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
    public List<OnlyFansModelCalculatedStatisticDto> getModelsStatistic(@PathVariable("id") long id,
                                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start)
            throws UserNotFoundedException {
        var user = repos.findById(id)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModels().stream().map(model -> {
            var statistic = model.getStatistics();

            var historical = statistic.stream()
                    .filter(s -> s.getMoment().after(start) && s.isGlobalPoint())
                    .collect(Collectors.toList());

            var calculated = StreamEx.of(historical)
                    .pairMap((p, n) ->
                            CalculatedStatisticDto.builder()
                                    .calculated(Statistic.subtract(n, p))
                                    .statistic(n)
                                    .build())
                    .collect(Collectors.toList());

            var current = statistic.size() > 0? statistic.get(statistic.size() - 1) : null;

            return OnlyFansModelCalculatedStatisticDto.builder()
                    .model(model)
                    .historicalCalculated(calculated)
                    .current(current)
                    .build();
        }).collect(Collectors.toList());
    }
}
