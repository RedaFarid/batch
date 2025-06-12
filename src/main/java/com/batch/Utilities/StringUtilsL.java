
package com.batch.Utilities;

import org.davidmoten.text.utils.WordWrap;

public class StringUtilsL {
    public static String textLimiter(String input, int limit) {
        return input == null ? "" : WordWrap.from(input).maxWidth(limit).insertHyphens(true).wrap();
    }
}
