package uapi.helper;

import com.google.common.base.Strings;
import uapi.InvalidArgumentException;
import uapi.KernelException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class StringHelper {

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};

    public static final String EMPTY    = "";

    private static final char VAR_START = '{';
    private static final char VAR_END   = '}';

    private static final int MASK_F     = 0xf;
    private static final int FOUR       = 4;
    private static final int SIXTEEN    = 16;

    private StringHelper() { }

    public static String makeString(String str, Object... args) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        StringBuilder buffer = new StringBuilder();
        boolean foundVarStart = false;
        int idxVar = 0;
        int tmpIdx = -1;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == VAR_START) {
                foundVarStart = true;
            } else if (c == VAR_END) {
                if (foundVarStart) {
                    if (tmpIdx != -1) {
                        idxVar = tmpIdx;
                    }
                    if (args.length <= idxVar) {
                        throw new IllegalArgumentException("The argument index is more than argument count");
                    }
                    buffer.append(args[idxVar]);
                    foundVarStart = false;
                    idxVar++;
                    tmpIdx = -1;
                } else {
                    buffer.append(c);
                }
            } else {
                if (foundVarStart) {
                    if (c >= '0' && c <= '9') {
                        if (tmpIdx == -1) {
                            tmpIdx = 0;
                        }
                        tmpIdx = tmpIdx * 10 + Character.getNumericValue(c);
                    } else {
                        buffer.append(VAR_START);
                        if (tmpIdx != -1) {
                            buffer.append(tmpIdx);
                            tmpIdx = -1;
                        } else {
                            buffer.append(c);
                        }
                        foundVarStart = false;
                    }
                } else {
                    buffer.append(c);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Make MD5 string based string array items
     *
     * @param   strs
     *          String array that used for make MD5 string
     * @return  MD5 string
     */
    public static String makeMD5(final String... strs) {
        if (strs == null || strs.length == 0) {
            throw new IllegalArgumentException();
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException ex) {
            throw new KernelException(ex);
        }
        for (String str : strs) {
            if (str == null) {
                throw new InvalidArgumentException("Not allow null string in the array {}", strs);
            }
            md.update(str.getBytes());
        }
        byte source[] = md.digest();
        char target[] = new char[SIXTEEN * 2];
        int k = 0;
        for (int i = 0; i < SIXTEEN; i++) {
            byte sbyte = source[i];
            target[k++] = HEX_DIGITS[sbyte >>> FOUR & MASK_F];
            target[k++] = HEX_DIGITS[sbyte & MASK_F];
        }
        return new String(target);
    }
}
