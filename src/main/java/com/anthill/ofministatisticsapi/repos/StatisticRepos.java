package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface StatisticRepos extends CommonRepository<Statistic> {

    @Query(value = "select s from Statistic s where s.moment >= :start and s.moment <= :end")
    List<Statistic> findAllByRange(@Param("start") Date start, @Param("end") Date end);
}
