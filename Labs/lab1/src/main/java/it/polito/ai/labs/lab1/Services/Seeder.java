package it.polito.ai.labs.lab1.Services;

import java.security.SecureRandom;

public class Seeder {
    private static Seeder singleton=null;
    private static SecureRandom randomizer;

    private Seeder(){
        randomizer=new SecureRandom();
    };
    public static Seeder getInstance(){
        if(singleton==null)
            singleton=new Seeder();
        return singleton;
    }

    public static String GenNextSeed(int lenght){
        if(lenght<=0) return null;
        byte bytes[]=new byte[lenght];
        randomizer.nextBytes(bytes);
        return new String(bytes);
    }
}
