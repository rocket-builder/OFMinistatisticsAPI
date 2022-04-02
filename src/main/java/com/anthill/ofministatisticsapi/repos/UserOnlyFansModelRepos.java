package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.UserOnlyFansModel;
import com.anthill.ofministatisticsapi.beans.id.UserOnlyFansModelId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserOnlyFansModelRepos extends CrudRepository<UserOnlyFansModel, UserOnlyFansModelId> {

    Optional<UserOnlyFansModel> findFirstByModel_IdAndUser_TelegramId(long modelId, long telegramId);

    @Transactional
    void deleteByModel_IdAndUser_Id(long modelId, long userId);
}
