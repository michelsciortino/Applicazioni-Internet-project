package it.polito.ai.labs.lab1.Services;

import it.polito.ai.labs.lab1.Services.Exceptions.ServiceException;
import it.polito.ai.labs.lab1.Services.Exceptions.UserAlreadyExistException;
import it.polito.ai.labs.lab1.Util.DigestGenerator;
import it.polito.ai.labs.lab1.ViewModels.User;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Database {
    private ConcurrentHashMap<String, User> users;
    private static Database singleton = null;

    public static Database getInstance() {
        if (singleton == null)
            singleton = new Database();
        return singleton;
    }

    private Database() {
        this.users = new ConcurrentHashMap<>();
    }

    public boolean AddUser(@NotNull User user) throws UserAlreadyExistException {
        User u = users.putIfAbsent(user.getMail(), user);
        if (u == null)
            return true;
        else
            throw new UserAlreadyExistException();
    }

    public boolean isRegistered(String mail) {
        return users.containsKey(mail);
    }

    public boolean VerifyLogin(String mail, String pass) throws ServiceException {
        User u = users.get(mail);
        if (u == null)
            return false;
        String salt = u.getSalt();
        try {
            String hashed_pass = DigestGenerator.Generate(pass, salt);
            if (u.getPass_hash().equals(hashed_pass))
                return true;
            else
                return false;
        } catch (Exception e) {
            throw new ServiceException();
        }
    }
}