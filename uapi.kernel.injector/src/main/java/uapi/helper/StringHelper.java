package uapi.helper;

import com.google.common.base.Strings;

public final class StringHelper {

    private static final char VAR_START = '{';
    private static final char VAR_END   = '}';

    public static String makeString(String str, Object... args) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        StringBuilder buffer = new StringBuilder();
        boolean foundVarStart = false;
        int idxVar = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == VAR_START) {
                foundVarStart = true;
            } else if (c == VAR_END) {
                if (foundVarStart) {
                    if (args.length <= idxVar) {
                        throw new IllegalArgumentException("");
                    }
                    buffer.append(args[idxVar]);
                    foundVarStart = false;
                    idxVar++;
                } else {
                    buffer.append(c);
                }
            } else {
                if (foundVarStart) {
                    buffer.append(VAR_START);
                    foundVarStart = false;
                }
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
}
