package com.anthill.ofministatisticsapi.beans;


import com.anthill.ofministatisticsapi.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
@Entity
public class User extends AbstractEntity {

    private String login, password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy="user")
    private List<OnlyFansModel> models;
}
