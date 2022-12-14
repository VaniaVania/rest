package com.ivan.restapplication.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ivan.restapplication.service.AuthService;
import com.ivan.restapplication.service.SavedUserService;
import com.ivan.restapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class MainController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public MainController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping()
    public String mainPage(){
        return "main";
    }

    @GetMapping("/artist")
    public String artist(){
        return "artist";
    }


    @ModelAttribute
    public void addAttributes(Model model) throws JsonProcessingException {
        if(authService.getToken()!=null){
            model.addAttribute("isAuthorized", true);
            model.addAttribute("profileImage", userService.showUserProfile().get("images")
                    .findValues("url")
                    .get(0)
                    .asText());
        }
    }
}
