package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlyFansModelRepos extends CommonRepository<OnlyFansModel> {

    Optional<OnlyFansModel> findByUrl(String url);
}
