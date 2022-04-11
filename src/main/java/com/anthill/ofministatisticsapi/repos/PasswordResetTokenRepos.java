package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.PasswordResetToken;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepos extends CommonRepository<PasswordResetToken> {

    Optional<PasswordResetToken> findByToken(String token);
}
