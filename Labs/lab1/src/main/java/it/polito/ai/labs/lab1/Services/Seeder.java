package it.polito.ai.labs.lab1.Services;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class Seeder {
    private static Seeder singleton = null;
    private static SecureRandom randomizer;

    public Seeder() {
        randomizer = new SecureRandom();
    }

    public String GenNextSeed(int lenght) {
        if (lenght <= 0) return null;
        byte[] bytes = new byte[lenght];
        randomizer.nextBytes(bytes);
        return new String(bytes);
    }
}
