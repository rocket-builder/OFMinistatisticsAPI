package com.anthill.ofministatisticsapi.beans;

import com.anthill.ofministatisticsapi.beans.id.UserOnlyFansModelId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties({"userAssoc", "statistics", "users", "alertUsers"})
public class OnlyFansModel extends AbstractEntity {
    private String name;
    private String url;
    private String avatarUrl;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<UserOnlyFansModel> userAssoc = new ArrayList<>();

    @OneToMany(mappedBy = "model", cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<Statistic> statistics = new ArrayList<>();

    public void addUser(User user){
        var assoc = UserOnlyFansModel.builder()
                .id(new UserOnlyFansModelId(user.getId(), this.getId()))
                .model(this)
                .user(user)
                .isNeedAlerts(true)
                .build();

        userAssoc.add(assoc);
    }

    public List<User> getUsers(){
        return userAssoc.stream()
                .map(UserOnlyFansModel::getUser)
                .collect(Collectors.toList());
    }
    public List<User> getAlertUsers(){
        return userAssoc.stream()
                .filter(UserOnlyFansModel::isNeedAlerts)
                .map(UserOnlyFansModel::getUser)
                .collect(Collectors.toList());
    }
}
