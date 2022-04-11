package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.PasswordResetToken;
import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelAlertedDto;
import com.anthill.ofministatisticsapi.beans.dto.onlyFansModel.OnlyFansModelCalculatedStatisticDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CredentialsDto;
import com.anthill.ofministatisticsapi.beans.dto.statistic.CurrentStatisticDto;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.*;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.PasswordResetTokenRepos;
import com.anthill.ofministatisticsapi.repos.UserOnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.security.MD5;
import com.anthill.ofministatisticsapi.services.CurrentStatisticService;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import com.anthill.ofministatisticsapi.services.MessageGeneratorService;
import com.anthill.ofministatisticsapi.services.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractController<User, UserRepos> {

    private final TelegramService telegramService;
    private final DataScrapperService scrapperService;
    private final CurrentStatisticService currentStatisticService;
    private final MessageGeneratorService messageGeneratorService;

    private final PasswordResetTokenRepos resetTokenRepos;
    private final OnlyFansModelRepos modelRepos;
    private final UserOnlyFansModelRepos userOnlyFansModelRepos;

    protected UserController(UserRepos repos, TelegramService telegramService,
                             OnlyFansModelRepos modelRepos,
                             DataScrapperService scrapperService,
                             CurrentStatisticService currentStatisticService,
                             MessageGeneratorService messageGeneratorService, PasswordResetTokenRepos resetTokenRepos,
                             UserOnlyFansModelRepos userOnlyFansModelRepos) {
        super(repos);
        this.telegramService = telegramService;
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
        this.currentStatisticService = currentStatisticService;
        this.messageGeneratorService = messageGeneratorService;
        this.resetTokenRepos = resetTokenRepos;
        this.userOnlyFansModelRepos = userOnlyFansModelRepos;
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
    public List<CurrentStatisticDto> getCurrentStatistic(@PathVariable("telegramId") long telegramId)
            throws UserNotFoundedException {
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

    @PostMapping("/{login}/createPasswordReset")
    public PasswordResetToken createPasswordReset(@PathVariable("login") String login)
            throws UserNotFoundedException {
        var user = repos.findByLogin(login)
                .orElseThrow(UserNotFoundedException::new);
        var token = resetTokenRepos.save(new PasswordResetToken(user));

        telegramService.sendMessage(
                messageGeneratorService.getPasswordReset(user.getTelegramId(), token));

        return token;
    }

    @PostMapping("/{telegramId}/model")
    public OnlyFansModel addModelOnUser(@PathVariable("telegramId") long telegramId, String url)
            throws UserNotFoundedException, CannotGetStatisticException, ResourceAlreadyExists, ResourceNotFoundedException {
        var user = repos.findByTelegramId(telegramId)
                .orElseThrow(UserNotFoundedException::new);

        var modelOptional = modelRepos.findByUrl(url);

        OnlyFansModel model;
        if(modelOptional.isPresent()){
            var existModel = modelOptional.get();
            if(existModel.getUsers().contains(user)){
                throw new ResourceAlreadyExists();
            }

            existModel.addUser(user);
            model = modelRepos.save(existModel);
        } else {
            var dto = scrapperService.getModelWithStatistic(url);

            var newModel = dto.getModel();
            newModel.addUser(user);
            newModel.setStatistics(List.of(dto.getStatistic()));

            model = modelRepos.save(newModel);
        }

        return model;
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
    public List<OnlyFansModelAlertedDto> getUserModels(@PathVariable("telegramId") long telegramId) throws UserNotFoundedException {
        var user = repos.findByTelegramId(telegramId)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModelsAlerted();
    }

    @GetMapping("/{id}/modelsStatistic")
    public List<OnlyFansModelCalculatedStatisticDto> getModelsStatistic(@PathVariable("id") long id,
                                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start)
            throws UserNotFoundedException {
        var user = repos.findById(id)
                .orElseThrow(UserNotFoundedException::new);

        return user.getModelsAlerted().stream()
                .map(model ->
                        currentStatisticService.getCurrentWithCalculated(model, start))
                .collect(Collectors.toList());
    }

    @PutMapping("/{telegramId}/model/{modelId}/alerts")
    public OnlyFansModel setModelAlerts(@PathVariable("telegramId") long telegramId,
                                        @PathVariable("modelId") long modelId,
                                        @RequestParam boolean enable)
            throws ResourceNotFoundedException {

        var assoc = userOnlyFansModelRepos.findFirstByModel_IdAndUser_TelegramId(modelId, telegramId)
                .orElseThrow(ResourceNotFoundedException::new);
        assoc.setNeedAlerts(enable);

        return userOnlyFansModelRepos.save(assoc).getModel();
    }

    @DeleteMapping("/{telegramId}/model/{modelId}")
    public ResponseEntity<String> deleteModelFromUser(@PathVariable("telegramId") long telegramId,
                                                      @PathVariable("modelId") long modelId){
        userOnlyFansModelRepos.deleteByModel_IdAndUser_TelegramId(modelId, telegramId);

        return new ResponseEntity<>("Successfully deleted!", HttpStatus.OK);
    }

    @DeleteMapping("/{telegramId}/models/list")
    public ResponseEntity<String> deleteModelFromUser(@PathVariable("telegramId") long telegramId,
                                                      @RequestBody List<Long> modelIds){
        modelIds.forEach(modelId ->
                userOnlyFansModelRepos.deleteByModel_IdAndUser_TelegramId(modelId, telegramId));

        return new ResponseEntity<>("Successfully deleted!", HttpStatus.OK);
    }
}
