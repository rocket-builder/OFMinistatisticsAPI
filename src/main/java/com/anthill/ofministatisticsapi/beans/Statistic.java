package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
@Entity
public class Statistic extends AbstractEntity {

    private String name;
    private int subscribersCount;
    private int likesCount;
    private Date moment;
}
