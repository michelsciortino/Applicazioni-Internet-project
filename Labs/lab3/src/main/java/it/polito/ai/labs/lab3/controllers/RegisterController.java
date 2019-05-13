package it.polito.ai.labs.lab3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


@Controller
@SessionAttributes("registerWarning")
public class RegisterController {

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(ModelMap model) {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String handleRegisterRequest(ModelMap model,
                                        @RequestParam String username,
                                        @RequestParam String password) {

        // i want to give this username and password ROLE_USER
        // hence credentials can login with spring security

        // done
        return "redirect:/login";
    }
}