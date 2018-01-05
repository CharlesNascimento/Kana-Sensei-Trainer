package com.kansus.kstrainer.repository;

import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.data.json.DefaultJSONHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class StrokesRepository implements Repository<StrokePattern> {

    private List<StrokePattern> cache = new ArrayList<>();

    public List<StrokePattern> getAll() {
        if (!cache.isEmpty()) {
            return cache;
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("strokes.json");
        cache = new DefaultJSONHandler().readArray(is, StrokePattern.class);
        return cache;
    }

    @Override
    public StrokePattern getById(int id) {
        List<StrokePattern> strokes = getAll();

        for (StrokePattern pattern : strokes) {
            if (pattern.getId() == id) {
                return pattern;
            }
        }

        return null;
    }
}
