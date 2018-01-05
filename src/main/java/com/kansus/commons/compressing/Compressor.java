package com.kansus.commons.compressing;

import java.util.zip.DataFormatException;

public interface Compressor {

    byte[] compress(byte[] data);

    String compress(String string);

    byte[] decompress(byte[] data) throws DataFormatException;
}