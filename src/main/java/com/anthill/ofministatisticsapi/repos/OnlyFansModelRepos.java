package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlyFansModelRepos extends CommonRepository<OnlyFansModel> {

    @Query(value = "select * from only_fans_model m where m.url=?1 order by m.id asc limit 1", nativeQuery = true)
    Optional<OnlyFansModel> findOldestByUrl(String url);
}
