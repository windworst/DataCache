package datacache;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new String(new BigInteger(1,md.digest()).toString(16));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }
}
