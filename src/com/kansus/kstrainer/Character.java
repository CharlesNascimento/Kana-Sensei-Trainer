package com.kansus.kstrainer;

import java.util.ArrayList;

public class Character {

	private String character;
	private int id;
	private ArrayList<String> strokes;
	
	public Character(String character, int id, ArrayList<String> strokes) {
		this.character = character;
		this.id = id;
		this.strokes = strokes;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<String> getStrokes() {
		return strokes;
	}
}
