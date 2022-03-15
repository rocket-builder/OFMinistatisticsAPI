package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties({"user", "statistics"})
public class OnlyFansModel extends AbstractEntity {
    private String name;
    private String url;
    private String avatarUrl;
    private boolean isNeedAlerts = true;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToMany(mappedBy = "model", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Statistic> statistics = new ArrayList<>();
}
