package com.anthill.ofministatisticsapi.beans.id;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOnlyFansModelId implements Serializable {

    private long user_id;
    private long model_id;
}
