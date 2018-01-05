package com.kansus.kstrainer.model;

public class StrokePattern {
	
	private int id;
	private String pattern;
	private int[] normalization;

	/**
	 * Creates a new strokes pattern.
	 */
	public StrokePattern() {

	}
	
	public StrokePattern(int id, String pattern, int[] normalization) {
		super();
		this.id = id;
		this.pattern = pattern;
		this.normalization = normalization;
	}

	public int getId() {
		return id;
	}

	public String getPattern() {
		return pattern;
	}

	public int[] getNormalization() {
		return normalization;
	}
}
