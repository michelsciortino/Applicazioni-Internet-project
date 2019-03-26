package it.polito.ai.labs.lab1.Controllers;

import it.polito.ai.labs.lab1.Services.Database;
import it.polito.ai.labs.lab1.Services.Exceptions.ServiceException;
import it.polito.ai.labs.lab1.Services.Exceptions.UserAlreadyExistException;
import it.polito.ai.labs.lab1.Services.Seeder;
import it.polito.ai.labs.lab1.Util.DigestGenerator;
import it.polito.ai.labs.lab1.ViewModels.LoginVm;
import it.polito.ai.labs.lab1.ViewModels.RegistrationVM;
import it.polito.ai.labs.lab1.ViewModels.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

import static it.polito.ai.labs.lab1.defines.PASSWORD_SEED_LEN;


@Controller
public class MainController {


    @GetMapping("/")
    public String getHome() {
        return "home";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@Valid RegistrationVM vm, BindingResult res, Model m) {
        if (res.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (ObjectError e : res.getAllErrors()) {
                errors.append(e.getDefaultMessage());
                errors.append("<br/>");
            }
            m.addAttribute("message", errors.toString());
            return "register";
        } else {

            if (!vm.getPass1().equals(vm.getPass2())) {
                m.addAttribute("message", "Le password non corrispondono.");
                return "register";
            }

            Database database = Database.getInstance();

            String seed = Seeder.getInstance().GenNextSeed(PASSWORD_SEED_LEN);

            String hashed_pass;

            try {
                hashed_pass = DigestGenerator.Generate(vm.getPass1(), seed);
            } catch (Exception e) {
                m.addAttribute("message", "Server error.");
                return "error";
            }

            try {
                database.AddUser(User.builder()
                        .mail(vm.getMail())
                        .name(vm.getName())
                        .surname(vm.getSurname())
                        .salt(new String(seed))
                        .pass_hash(hashed_pass)
                        .build());
            } catch (UserAlreadyExistException e) {
                m.addAttribute("message", e.getMessage());
                return "register";
            }

            m.addAttribute("message", "user " + vm.getMail() + " has been properly registered");
        }
        return "home";
    }

    @PostMapping("/login")
    public String processLoginForm(@Valid LoginVm vm, BindingResult res, Model m) {
        if (res.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (ObjectError e : res.getAllErrors()) {
                errors.append(e.getDefaultMessage());
                errors.append("&#10;");
            }
            m.addAttribute("message", errors.toString());
            return "login";
        } else {
            Database d = Database.getInstance();
            try {
                if (d.VerifyLogin(vm.getMail(), vm.getPass())) {
                    m.addAttribute("message", "user " + vm.getMail() + " now logged on.");
                    return "home";
                } else {
                    m.addAttribute("message", "Invalid credentials.");
                    return "login";
                }
            } catch (ServiceException e) {
                m.addAttribute("message", e.getMessage());
                return "error";
            }
        }
    }
}
