import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Scanner;
import java.security.SecureRandom;


public class rsa {

    public static String encrypt(String plainMessage, File publicKeyFile) {
        String publicKey;
        try {
            Scanner reader = new Scanner(publicKeyFile);
            publicKey = reader.nextLine().replace("\"", "");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        String[] keys = publicKey.split(";");
        Key k = new Key(new BigInteger(keys[0]), new BigInteger(keys[1]));

        byte[] encryptedMessage = encrypt(plainMessage, k);
        String out = Base64.getEncoder().encodeToString(encryptedMessage);
        return out;
    }

    private static BigInteger crypt(BigInteger message, Key key) {
        return message.modPow(key.getE(), key.getN());
    }

    public static byte[] encrypt(String plainMessage, Key key) {
        byte[] bytes = plainMessage.getBytes(Charset.defaultCharset());
        return crypt(new BigInteger(bytes), key).toByteArray();
    }

    public static String decrypt(byte[] cipher, Key key) {
        byte[] msg = crypt(new BigInteger(cipher), key).toByteArray();
        return new String(msg);
    }

    public static String decrypt(String encryptedMessage, File privateKeyFile) {
        String key;
        try {
            Scanner reader = new Scanner(privateKeyFile);
            key = reader.nextLine().replace("\"", "");
        } catch (Exception e) {
            return e.getMessage();
        }
        String[] keys = key.split(";");
        Key k = new Key(new BigInteger(keys[0]), new BigInteger(keys[1]));

        return decrypt(Base64.getDecoder().decode(encryptedMessage), k);
    }

    public static class Key {
        private final BigInteger n;
        private final BigInteger e;

        public Key(BigInteger n, BigInteger e) {
            this.n = n;
            this.e = e;
        }

        public BigInteger getN() {
            return n;
        }

        public BigInteger getE() {
            return e;
        }
    }

    public static class RSA {
        private final BigInteger p;
        private final BigInteger q;
        private final BigInteger n;
        private final BigInteger t;
        private final BigInteger e;
        private final BigInteger d;
        private final Key publicKey;
        private final Key privateKey;

        public RSA(int keyLength) {
            SecureRandom randomGenerator = new SecureRandom();

            p = new BigInteger(keyLength, 100, randomGenerator).nextProbablePrime();
            q = new BigInteger(keyLength, 100, randomGenerator).nextProbablePrime();

            n = p.multiply(q);
            t = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
            e = getCoPrime(t);
            d = e.modInverse(t);

            publicKey = new Key(n, e);

            privateKey = new Key(n, d);
        }

        public boolean isCoPrime(BigInteger c, BigInteger n) {
            BigInteger one = new BigInteger("1");
            return c.gcd(n).equals(one);
        }

        public BigInteger getCoPrime(BigInteger n) {
            BigInteger result = new BigInteger(n.toString());
            BigInteger one = new BigInteger("1");
            BigInteger two = new BigInteger("2");
            result = result.subtract(two);

            while (result.intValue() > 1) {
                if (result.gcd(n).equals(one))
                    break;
                result = result.subtract(one);
            }

            return result;
        }

        public BigInteger getP() {
            return p;
        }

        public BigInteger getQ() {
            return q;
        }

        public BigInteger getN() {
            return n;
        }

        public BigInteger getT() {
            return t;
        }

        public BigInteger getE() {
            return e;
        }

        public BigInteger getD() {
            return d;
        }

        public Key getPublicKey() {
            return publicKey;
        }

        public Key getPrivateKey() {
            return privateKey;
        }
    }
    /*
    public static String encrypt(String plainMessage, File publicKeyFile){
        String publicKey = null;
        String out = null;
        try {
            Scanner reader = new Scanner(publicKeyFile);
            publicKey = reader.nextLine().replace("\"", "");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        try {
             out = Base64.getEncoder().encodeToString(encrypt(plainMessage, publicKey));
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return out;
    }

    public static String decrypt(String encryptedMessage, File privateKeyFile){
        String privateKey = null;
        String out = null;
        try {
            Scanner reader = new Scanner(privateKeyFile);
            privateKey = reader.nextLine().replace("\"", "");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        try {
            out = decrypt(Base64.getDecoder().decode(encryptedMessage), privateKey);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return out;
    }

    private static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    private static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    private static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    private static String decrypt(byte[] data, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        return new String(cipher.doFinal(data));
    }
    */
}
