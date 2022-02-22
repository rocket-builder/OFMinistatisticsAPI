package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepos extends CommonRepository<Statistic> {

    @Query(value = "select s from Statistic s where s.moment >= :start and s.moment <= :end and s.model.id=:id")
    List<Statistic> findAllByRange(@Param("start") Date start, @Param("end") Date end, @Param("id") long id);

    @Query(value = "select * from statistic s where s.model_id=?1 order by id desc limit 1", nativeQuery = true)
    Optional<Statistic> findLastByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and s.moment = CURDATE()",
            nativeQuery = true)
    List<Statistic> findTodayByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and s.moment >= DATE_SUB(DATE(NOW()), INTERVAL 1 DAY)",
            nativeQuery = true)
    List<Statistic> findYesterdayByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and s.moment >= DATE_SUB(DATE(NOW()), INTERVAL 7 DAY)",
            nativeQuery = true)
    List<Statistic> findLastWeekByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and MONTH(s.moment) = MONTH(NOW()) - 1",
            nativeQuery = true)
    List<Statistic> findLastMonthByModel(long modelId);
}
