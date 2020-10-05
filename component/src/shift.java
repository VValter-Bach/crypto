import java.io.File;
import java.util.Scanner;

public class shift {

    public static String encrypt(String plainMessage, File keyFile) {
        int shift;
        try {
            Scanner reader = new Scanner(keyFile);
            shift = Integer.parseInt(reader.nextLine());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        CaesarCipher cc = new CaesarCipher(shift);
        return cc.encrypt(plainMessage);
    }

    public static String decrypt(String encryptedMessage, File keyFile) {
        int shift;
        try {
            Scanner reader = new Scanner(keyFile);
            shift = Integer.parseInt(reader.nextLine());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        CaesarCipher cc = new CaesarCipher(shift);
        return cc.decrypt(encryptedMessage);
    }

    public static class CaesarCipher {
        private final int key;

        public CaesarCipher(int key) {
            this.key = key;
        }

        public String encrypt(String plainText) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < plainText.length(); i++) {
                char character = (char) (plainText.codePointAt(i) + key);
                stringBuilder.append(character);
            }

            return stringBuilder.toString();
        }

        public String decrypt(String cipherText) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < cipherText.length(); i++) {
                char character = (char) (cipherText.codePointAt(i) - key);
                stringBuilder.append(character);
            }

            return stringBuilder.toString();
        }
    }

 /*    public static String encrypt(String plainMessage, File keyFile) {
       int shift = -1;
        String out = null;
        try {
            Scanner reader = new Scanner(keyFile);
            shift = Integer.parseInt(reader.nextLine());
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        out = new String();
        for(int i = 0; i < plainMessage.length(); i++){
            char sign = plainMessage.charAt(i);
            if (sign >= 'a' && sign <= 'z'){
                sign = (char)(sign + shift);
                if(sign < 'a') {
                    sign = (char) (((sign - 'a') % 26) + 'z' + 1);
                }
                else if(sign > 'z'){
                    sign = (char) (((sign - 'z') % 26) + 'a' - 1);
                }
            }
            else if(sign >= 'A' && sign <= 'Z'){
                sign = (char)(sign + shift);
                if(sign < 'A') {
                    sign = (char) (((sign - 'A') % 26) + 'Z' + 1);
                }
                else if(sign > 'Z'){
                    sign = (char) (((sign - 'Z') % 26) + 'A' - 1);
                }
            }
            out += sign;
        }
        return out;

    }

    public static String decrypt(String encryptedMessage, File keyFile){
        int shift = -1;
        String out = null;
        try {
            Scanner reader = new Scanner(keyFile);
            shift = Integer.parseInt(reader.nextLine().stripTrailing().stripLeading());
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        out = new String();
        for (int i = 0; i < encryptedMessage.length(); i++){
            char sign = encryptedMessage.charAt(i);
            if (sign >= 'a' && sign <= 'z'){
                sign = (char)(sign - shift);
                if(sign < 'a') {
                    sign = (char) (((sign - 'a') % 26) + 'z' + 1);
                }
                else if(sign > 'z'){
                    sign = (char) (((sign - 'z') % 26) + 'a' - 1);
                }
            }
            else if(sign >= 'A' && sign <= 'Z'){
                sign = (char)(sign - shift);
                if(sign < 'A') {
                    sign = (char) (((sign - 'A') % 26) + 'Z' + 1);
                }
                else if(sign > 'Z'){
                    sign = (char) (((sign - 'Z') % 26) + 'A' - 1);
                }
            }
            out += sign;

        }
        return out;
    }*/
}
