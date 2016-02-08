package uapi.helper;

import com.google.common.base.Strings;

public final class StringHelper {

    public static final String EMPTY    = "";

    private static final char VAR_START = '{';
    private static final char VAR_END   = '}';

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
}
