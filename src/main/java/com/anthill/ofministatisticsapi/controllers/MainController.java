package com.anthill.ofministatisticsapi.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Index")
@Controller
public class MainController {

    @GetMapping("/")
    public String index(){

        return "swagger-ui.html";
    }
}
