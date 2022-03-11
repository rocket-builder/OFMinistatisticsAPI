package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.User;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepos extends CommonRepository<User> {

    Optional<User> findByTelegramId(long telegramId);
    Optional<User> findByLogin(String login);

    Optional<User> findFirstByLoginOrTelegramId(String login, long telegramId);
}
