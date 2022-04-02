package com.anthill.ofministatisticsapi.beans.dto.onlyFansModel;

import com.anthill.ofministatisticsapi.beans.OnlyFansModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@JsonIgnoreProperties({"userAssoc", "statistics", "users", "alertUsers"})
public class OnlyFansModelAlertedDto extends OnlyFansModel {

    private boolean needAlerts;
}
