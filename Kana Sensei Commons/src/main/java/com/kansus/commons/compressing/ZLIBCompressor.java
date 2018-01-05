package com.kansus.commons.compressing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZLIBCompressor implements Compressor {

    private static final Logger LOG = Logger.getLogger(ZLIBCompressor.class.getName());

    public byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] output = outputStream.toByteArray();
        LOG.info("Original: " + data.length + " bytes");
        LOG.info("Compressed: " + output.length + " bytes");

        return output;
    }

    @Override
    public String compress(String string) {
        return null;
    }

    public byte[] decompress(byte[] data) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] output = outputStream.toByteArray();
        LOG.info("Original: " + data.length);
        LOG.info("Compressed: " + output.length);

        return output;
    }
}