package com.kansus.kstrainer.data.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface JSONHandler {

    <T> List<T> readArray(InputStream stream, Class<T> type);

    void saveToFile(Object object, File file) throws IOException;
}

