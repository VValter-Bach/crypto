public class shift_cracker {
    public static String decrypt(String encryptedMessage) {

        char[] sourceText = new char[encryptedMessage.length()];
        int[] unicode = new int[encryptedMessage.length()];
        int[] unicodeCopy = new int[encryptedMessage.length()];

        for (int count = 0; count < encryptedMessage.length(); count++) {
            sourceText[count] = encryptedMessage.charAt(count);
        }

        String hex;
        int dec;

        for (int count = 0; count < sourceText.length; count++) {
            hex = Integer.toHexString(sourceText[count]);
            dec = Integer.parseInt(hex, 16);
            unicode[count] = dec;
            unicodeCopy[count] = dec;
        }
        String sol = new String();
        for (int shift = 1; shift <= 25; shift++) {
            sol += smartShift(shift, unicode, unicodeCopy) + ",";
        }
        return sol;
    }

    private static String smartShift(int shift, int[] unicode, int[] unicodeCopy) {
        for (int x = 0; x <= unicode.length - 1; x++) {
            unicodeCopy[x] = unicode[x];
            unicodeCopy[x] += shift;
            if (unicodeCopy[x] > 90) {
                unicodeCopy[x] -= 26;
            } else if (unicodeCopy[x] < 65){
                unicodeCopy[x] += 25;
            }
        }

        String[] processed = new String[unicode.length];
        char[] finalProcess = new char[unicode.length];

        for (int count = 0; count < processed.length; count++) {
            processed[count] = Integer.toHexString(unicodeCopy[count]);
            int hexToInt = Integer.parseInt(processed[count], 16);
            char intToChar = (char) hexToInt;
            finalProcess[count] = intToChar;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (char character : finalProcess) {
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }
}
