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
        int idxDef = 0;
        String idxSpecified = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == VAR_START) {
                foundVarStart = true;
            } else if (c == VAR_END) {
                if (foundVarStart) {
                    if (! Strings.isNullOrEmpty(idxSpecified)) {
                        idxDef = Integer.parseInt(idxSpecified);
                        idxSpecified = "";
                    }
                    if (args.length <= idxDef) {
                        throw new IllegalArgumentException("No parameter for index - " + idxDef);
                    }
                    buffer.append(args[idxDef]);
                    foundVarStart = false;
                    idxDef++;
                } else {
                    buffer.append(c);
                }
            } else {
                if (foundVarStart) {
                    if (c >= '0' && c <= '9') {
                        idxSpecified += c;
                        continue;
                    }
                    buffer.append(VAR_START).append(idxSpecified);
                    idxSpecified = "";
                    foundVarStart = false;
                }
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
}
