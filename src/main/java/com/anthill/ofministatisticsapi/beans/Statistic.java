package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

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

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date moment;

    @Column(name = "global")
    private boolean isGlobalPoint = false;

    public static Statistic subtract(Statistic s1, Statistic s2){
        var result = new Statistic();
        result.setName(s1.getName());
        result.setModel(s1.getModel());

        result.setSubscribersCount(s1.getSubscribersCount() - s2.getSubscribersCount());
        result.setLikesCount(s1.getLikesCount() - s2.getLikesCount());

        result.setMoment(s1.getMoment());

        return result;
    }

    public boolean isZero(){
        return subscribersCount == 0 && likesCount == 0;
    }
}
