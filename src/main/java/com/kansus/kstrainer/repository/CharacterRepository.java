package com.kansus.kstrainer.repository;

import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.data.json.DefaultJSONHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CharacterRepository implements Repository<Character> {

    private List<Character> cache = new ArrayList<>();

    public List<Character> getAll() {
        if (!cache.isEmpty()) {
            return cache;
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("characters.json");
        cache = new DefaultJSONHandler().readArray(is, Character.class);
        return cache;
    }

    @Override
    public Character getById(int id) {
        List<Character> characters = getAll();

        for (Character character : characters) {
            if (character.getId() == id) {
                return character;
            }
        }

        return null;
    }
}
