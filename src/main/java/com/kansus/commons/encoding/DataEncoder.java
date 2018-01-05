package com.kansus.commons.encoding;

public interface DataEncoder {

    String encode(byte[] bytes);

    byte[] decode(String text);
}
