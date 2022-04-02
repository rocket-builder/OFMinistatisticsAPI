package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties({"model"})
public class Statistic extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name="model_id", nullable=false)
    private OnlyFansModel model;

    private int subscribersCount;
    private int likesCount;
    private int videosCount;
    private int photosCount;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date moment;

    @Column(name = "global")
    private boolean isGlobalPoint = false;

    public static Statistic subtract(Statistic s1, Statistic s2){
        var result = new Statistic();
        result.setModel(s1.getModel());

        result.setSubscribersCount(s1.getSubscribersCount() - s2.getSubscribersCount());
        result.setLikesCount(s1.getLikesCount() - s2.getLikesCount());
        result.setPhotosCount(s1.getPhotosCount() - s2.getPhotosCount());
        result.setVideosCount(s1.getVideosCount() - s2.getVideosCount());

        result.setMoment(s1.getMoment());

        return result;
    }

    public boolean isZero(){
        return subscribersCount == 0 && likesCount == 0;
    }
}
