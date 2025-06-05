package labs.pinheiro.springbootconsole.arearestrita.util;

import java.util.Base64;

public abstract class Util {
    
    public static String base64Decode(String status) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(status);
            return new String(decodedBytes);
        } catch (Exception e) {
            // Ok, nada aqui
        }
        return status;
    }
}
