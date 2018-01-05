package com.kansus.commons.encoding;

import java.util.Base64;

public class Base64DataEncoder implements DataEncoder {

    @Override
    public String encode(byte[] bytes) {
        String string = Base64.getEncoder().encodeToString(bytes);
        System.out.println(string);
        return string;
    }

    @Override
    public byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }
}
