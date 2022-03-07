package com.anthill.ofministatisticsapi.beans.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter @Setter
public class ScrapperStatisticDto {

    private String name;
    private String avatarUrl;

    private int subscribersCount;
    private int likesCount;
    private int videosCount;
    private int photosCount;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date moment;
}
