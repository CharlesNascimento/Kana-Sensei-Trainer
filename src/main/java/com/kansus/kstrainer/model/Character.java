package com.kansus.kstrainer.model;

/**
 * Represents a japanese character.
 * <p>
 * Created by Charles on 02/11/2015.
 */
public class Character {

    public static final int ALPHABET_HIRAGANA = 1;
    public static final int ALPHABET_KATAKANA = 2;

    private int id;

    private int alphabet;

    private String kana;

    private String romaji;

    private String hexCode;

    private int strokesPattern;

    private int strokesPatternAlt;

    /**
     * Creates a new character.
     */
    public Character() {

    }

    /**
     * Creates a new character.
     *
     * @param id       The id of the character.
     * @param alphabet The alphabet the character belongs to.
     * @param kana     The character itself.
     * @param romaji   The pronunciation of the character.
     * @param hexCode  The hexadecimal code that represents this character.
     */
    public Character(int id, int alphabet, String kana, String romaji, String hexCode, int strokesPattern, int strokesPatternAlt) {
        this.id = id;
        this.alphabet = alphabet;
        this.kana = kana;
        this.romaji = romaji;
        this.hexCode = hexCode;
        this.strokesPattern = strokesPattern;
        this.strokesPatternAlt = strokesPatternAlt;
    }

    /**
     * @return The id of this character.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The alphabet this character belongs to.
     */
    public int getAlphabet() {
        return alphabet;
    }

    /**
     * @return The character itself.
     */
    public String getKana() {
        return kana;
    }

    /**
     * @return The pronunciation of the character.
     */
    public String getRomaji() {
        return romaji;
    }

    /**
     * @return The hexadecimal code that represents this character.
     */
    public String getHexCode() {
        return hexCode;
    }

    /**
     * @return The strokes pattern of this character.
     */
    public int getStrokesPattern() {
        return strokesPattern;
    }

    /**
     * @return The alternative strokes pattern of this character.
     */
    public int getStrokesPatternAlt() {
        return strokesPatternAlt;
    }

    @Override
    public String toString() {
        return kana + " (" + romaji + ")";
    }
}
