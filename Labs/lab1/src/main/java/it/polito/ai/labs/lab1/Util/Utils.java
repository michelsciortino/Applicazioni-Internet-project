package it.polito.ai.labs.lab1.Util;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class Utils {

    public static void AddErrors(Model m, BindingResult res){
        StringBuilder errors = new StringBuilder();
        for (ObjectError e : res.getAllErrors()) {
            errors.append(e.getDefaultMessage());
            errors.append("<br/>");
        }
        m.addAttribute("message", errors.toString());
    }
}
