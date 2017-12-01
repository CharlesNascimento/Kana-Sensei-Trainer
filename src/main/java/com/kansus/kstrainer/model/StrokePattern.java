package com.kansus.kstrainer.model;

public class StrokePattern {
	
	private int id;
	private String name;
	private int[] normalization;
	
	public StrokePattern(int id, String name, int[] normalization) {
		super();
		this.id = id;
		this.name = name;
		this.normalization = normalization;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int[] getNormalization() {
		return normalization;
	}
}
