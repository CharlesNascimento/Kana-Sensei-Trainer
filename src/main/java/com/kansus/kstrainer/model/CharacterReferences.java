package com.kansus.kstrainer.model;

import java.util.List;

public class CharacterReferences {

    private int characterId;

    private List<String> references;

    public CharacterReferences(int characterId, List<String> references) {
        this.characterId = characterId;
        this.references = references;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }
}
