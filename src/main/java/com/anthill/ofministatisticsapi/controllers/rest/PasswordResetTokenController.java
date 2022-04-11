package com.anthill.ofministatisticsapi.controllers.rest;

import com.anthill.ofministatisticsapi.beans.PasswordResetToken;
import com.anthill.ofministatisticsapi.controllers.AbstractController;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.repos.PasswordResetTokenRepos;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PasswordReset")
@RequestMapping("/passwordResetToken")
@RestController
public class PasswordResetTokenController extends AbstractController<PasswordResetToken, PasswordResetTokenRepos> {

    protected PasswordResetTokenController(PasswordResetTokenRepos repos) {
        super(repos);
    }

    @GetMapping("/by")
    public PasswordResetToken getByToken(@RequestParam String token) throws ResourceNotFoundedException {

        return repos.findByToken(token)
                .orElseThrow(ResourceNotFoundedException::new);
    }
}
