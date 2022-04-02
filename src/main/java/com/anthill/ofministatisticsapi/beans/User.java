package com.anthill.ofministatisticsapi.beans;


import com.anthill.ofministatisticsapi.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties({"modelsAssoc"})
public class User extends AbstractEntity {

    private String login, password;
    private long telegramId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user")
    private List<UserOnlyFansModel> modelsAssoc= new ArrayList<>();

    public List<OnlyFansModel> getModels(){
        return modelsAssoc.stream()
                .map(UserOnlyFansModel::getModel)
                .collect(Collectors.toList());
    }
    public void setModels(List<OnlyFansModel> models){
        this.modelsAssoc = models.stream()
                .map(model ->
                        UserOnlyFansModel.builder()
                                .user(this)
                                .model(model)
                                .build())
                .collect(Collectors.toList());
    }
}
