package com.tacz.guns.util;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class UriEncoder {
    private static final CharsetDecoder UTF8Decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT);
    private static final String SAFE_CHARS = "-_.!~*'()@:$&,;=[]/";
    private static final Escaper escaper = new PercentEscaper(SAFE_CHARS, false);

    public static String encode(String uri) {
        return escaper.escape(uri)
                .replace(" ", "%20")
                .replace("[", "%5B")
                .replace("]", "%5D");
    }

    public static String decode(ByteBuffer buff) throws CharacterCodingException {
        CharBuffer chars = UTF8Decoder.decode(buff);
        return chars.toString();
    }

    public static String decode(String buff) {
        return URLDecoder.decode(buff, StandardCharsets.UTF_8);
    }
}
