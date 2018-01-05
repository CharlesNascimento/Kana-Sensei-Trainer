package com.kansus.kstrainer.data.json;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DefaultJSONHandler implements JSONHandler {

    public <T> List<T> readArray(InputStream stream, Class<T> type) {
        List<T> objects = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            objects = mapper.readValue(stream, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objects;
    }

    public void saveToFile(Object object, File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, object);
    }
}