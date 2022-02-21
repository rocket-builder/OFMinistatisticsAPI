package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties({"model"})
public class Statistic extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name="model_id", nullable=false)
    private OnlyFansModel model;

    private String name;
    private int subscribersCount;
    private int likesCount;
    private Date moment;

    public void subtract(Statistic statistic){
        this.subscribersCount -= statistic.getSubscribersCount();
        this.likesCount -= statistic.getLikesCount();
    }
}
