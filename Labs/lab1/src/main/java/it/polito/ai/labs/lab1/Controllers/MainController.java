package it.polito.ai.labs.lab1.Controllers;

import it.polito.ai.labs.lab1.Services.Database;
import it.polito.ai.labs.lab1.Services.Exceptions.ServiceException;
import it.polito.ai.labs.lab1.Services.Exceptions.UserAlreadyExistException;
import it.polito.ai.labs.lab1.Services.Seeder;
import it.polito.ai.labs.lab1.Util.DigestGenerator;
import it.polito.ai.labs.lab1.ViewModels.LoginVM;
import it.polito.ai.labs.lab1.ViewModels.RegistrationVM;
import it.polito.ai.labs.lab1.ViewModels.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static it.polito.ai.labs.lab1.Util.Utils.AddErrors;
import static it.polito.ai.labs.lab1.defines.PASSWORD_SEED_LEN;

@Controller
public class MainController {

    private Database database;

    private Seeder seeder;

    @Autowired
    public MainController(Database database,Seeder seeder){
        this.database=database;
        this.seeder=seeder;
    }

    @GetMapping("/")
    public String getHome() {
        return "home";
    }


    @GetMapping("/register")
    public String getRegistrationPage(Model model) {
        model.addAttribute("registrationVM", new RegistrationVM());
        return "register";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("loginVM", new LoginVM());
        return "login";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@Valid @ModelAttribute("registrationVM") RegistrationVM registrationVM, BindingResult res, Model m, HttpServletResponse httpServletResponse) {
        if (res.hasErrors()) {
            //AddErrors(m,res);
            return "register";
        } else {
            if (!registrationVM.getPass1().equals(registrationVM.getPass2())) {
                m.addAttribute("message", "Le password non corrispondono.");
                return "register";
            }

            if(!registrationVM.isPrivacyConsentAccepted()){
                m.addAttribute("message","Devi accettare il consenso alle regole sulla privacy.");
                return "register";
            }

            String seed = seeder.GenNextSeed(PASSWORD_SEED_LEN);
            String hashed_pass;

            try {
                hashed_pass = DigestGenerator.Generate(registrationVM.getPass1(), seed);
            } catch (Exception e) {
                m.addAttribute("message", "Server error.");
                return "error";
            }

            try {
                database.AddUser(User.builder()
                        .mail(registrationVM.getMail())
                        .name(registrationVM.getName())
                        .surname(registrationVM.getSurname())
                        .salt(seed)
                        .pass_hash(hashed_pass)
                        .build());
            } catch (UserAlreadyExistException e) {
                m.addAttribute("message", e.getMessage());
                return "register";

            }
            m.addAttribute("message", "user " + registrationVM.getMail() + " has been properly registered");
        }
        return "home"; //"redirect:/";
    }

    @PostMapping("/login")
    public String processLoginForm(@Valid @ModelAttribute("loginVM") LoginVM loginVM, BindingResult res, Model m) {
        if (res.hasErrors()) {
            m.addAttribute("message", "Invalid credentials.");
            //AddErrors(m,res);
            return "login";
        } else {
            try {
                if (database.VerifyLogin(loginVM.getMail(), loginVM.getPass())) {
                    m.addAttribute("message", "user " + loginVM.getMail() + " now logged on.");
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
