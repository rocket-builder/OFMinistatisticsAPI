package com.anthill.ofministatisticsapi.beans;

import com.anthill.ofministatisticsapi.beans.id.UserOnlyFansModelId;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserOnlyFansModel implements Serializable {

    @EmbeddedId
    private UserOnlyFansModelId id;

    @Id
    @MapsId("user_id")
    @ManyToOne
    private User user;

    @Id
    @MapsId("model_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private OnlyFansModel model;

    @ColumnDefault("true")
    private boolean isNeedAlerts;
}
