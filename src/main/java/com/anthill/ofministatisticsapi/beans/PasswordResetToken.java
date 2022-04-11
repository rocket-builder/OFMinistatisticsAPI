package com.anthill.ofministatisticsapi.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
@Entity
public class PasswordResetToken extends AbstractEntity {

    private static final int EXPIRATION_HOURS = 2;

    @ManyToOne
    private User user;

    private String token;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expired;

    public PasswordResetToken(User user){
        this.user = user;

        expired = Date.from(
                new Date().toInstant()
                        .plus(EXPIRATION_HOURS, ChronoUnit.HOURS));

        token = UUID.randomUUID().toString();
    }

    public boolean isAlreadyExpired(){

        return new Date().after(expired);
    }
}
