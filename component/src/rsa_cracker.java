import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class rsa_cracker {
    public static String decrypt(String encryptedMessage) {
        //return "mama";
        BigInteger e = BigInteger.valueOf(12371);
        BigInteger n = new BigInteger("517815623413379");

        BigInteger cipher = new BigInteger(encryptedMessage.getBytes());
        return execute(e, n, cipher);
    }

    private static String execute(BigInteger e, BigInteger n, BigInteger cipher) {
        BigInteger p, q, d;
        List<BigInteger> factorList = factorize(n);

        if (factorList.size() != 2) {
            return "cannot determine factors p and q";
        }

        p = factorList.get(0);
        q = factorList.get(1);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        d = e.modInverse(phi);
        return new String(cipher.modPow(d, n).toByteArray());
    }

    private static List<BigInteger> factorize(BigInteger n) {
        BigInteger two = BigInteger.valueOf(2);
        List<BigInteger> factorList = new LinkedList<>();

        if (n.compareTo(two) < 0) {
            throw new IllegalArgumentException("must be greater than one");
        }

        while (n.mod(two).equals(BigInteger.ZERO)) {
            factorList.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger factor = BigInteger.valueOf(3);
            while (factor.multiply(factor).compareTo(n) <= 0) {
                if (n.mod(factor).equals(BigInteger.ZERO)) {
                    factorList.add(factor);
                    n = n.divide(factor);
                } else {
                    factor = factor.add(two);
                }
            }
            factorList.add(n);
        }

        return factorList;
    }
}
