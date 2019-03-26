package it.polito.ai.labs.lab1.Util;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DigestGenerator {

    public static String Generate(@NotNull String string, @NotNull String seed) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        return new String(d.digest((string + seed).getBytes(StandardCharsets.UTF_8)));
    }
}
