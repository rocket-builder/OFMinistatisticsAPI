package com.anthill.ofministatisticsapi.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResourceNotFoundedException extends Exception {
    private String resource;

    public ResourceNotFoundedException(String resource){
        this.resource = resource;
    }
}
