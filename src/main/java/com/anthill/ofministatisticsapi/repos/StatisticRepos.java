package com.anthill.ofministatisticsapi.repos;

import com.anthill.ofministatisticsapi.beans.Statistic;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepos extends CommonRepository<Statistic> {

    @Query(value = "select * from statistic s where s.model_id=?1 order by id desc limit 1", nativeQuery = true)
    Optional<Statistic> findLastByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and s.global=true order by s.id desc limit 1",
            nativeQuery = true)
    Optional<Statistic> findLastGlobalPointByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and " +
            "s.global=true and s.moment = DATE_SUB(DATE(NOW()), INTERVAL 1 DAY) " +
            "order by s.id desc limit 1", nativeQuery = true)
    Optional<Statistic> findLastYesterdayGlobalPointByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and " +
            "s.global=true and s.moment >= DATE_SUB(DATE(NOW()), INTERVAL ?2 DAY) " +
            "order by s.id desc", nativeQuery = true)
    List<Statistic> findLastGlobalPointsByModelAndDaysCount(long modelId, int days);

    @Query(value = "select * from statistic s where s.model_id=?1 and " +
            "s.global=true and s.moment >= DATE_SUB(date(NOW()), interval ?2 month) group by month(s.moment) " +
            "order by s.id desc", nativeQuery = true)
    List<Statistic> findLastGlobalPointsByModelAndMonthCount(long modelId, int month);

    @Query(value = "select * from statistic s where s.model_id=?1 and DATE(s.moment) = CURDATE() order by id asc limit 1",
            nativeQuery = true)
    Optional<Statistic> findTodayFirstByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and s.moment >= DATE_SUB(DATE(NOW()), INTERVAL 7 DAY)",
            nativeQuery = true)
    List<Statistic> findLastWeekByModel(long modelId);

    @Query(value = "select * from statistic s where s.model_id=?1 and MONTH(s.moment) >= MONTH(NOW()) - 1",
            nativeQuery = true)
    List<Statistic> findLastMonthByModel(long modelId);
}
