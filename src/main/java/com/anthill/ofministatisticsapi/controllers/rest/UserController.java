package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.IncorrectPasswordException;
import com.anthill.ofministatisticsapi.exceptions.LoginAlreadyTakenException;
import com.anthill.ofministatisticsapi.exceptions.ResourceAlreadyExists;
import com.anthill.ofministatisticsapi.exceptions.UserNotFoundedException;
import com.anthill.ofministatisticsapi.repos.OnlyFansModelRepos;
import com.anthill.ofministatisticsapi.repos.UserRepos;
import com.anthill.ofministatisticsapi.services.DataScrapperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.anthill.ofministatisticsapi.security.MD5;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractController<User, UserRepos> {

    private final OnlyFansModelRepos modelRepos;
    private final DataScrapperService scrapperService;

    protected UserController(UserRepos repos, OnlyFansModelRepos modelRepos, DataScrapperService scrapperService) {
        super(repos);
        this.modelRepos = modelRepos;
        this.scrapperService = scrapperService;
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

    @PostMapping("/{id}/model")
    public OnlyFansModel addModel(@PathVariable("id") long id, String url)
            throws ResourceAlreadyExists, UserNotFoundedException, IOException, URISyntaxException {
        if(modelRepos.existsByUrl(url)){
            throw new ResourceAlreadyExists();
        }

        var user = repos.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundedException();
        }

        var statistics = scrapperService.getStatistics(url);

        var model = new OnlyFansModel();
        model.setName(statistics.getName());
        model.setStatistics(List.of(statistics));
        model.setUser(user.get());

        return modelRepos.save(model);
    }
}
