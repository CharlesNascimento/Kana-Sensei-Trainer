package com.kansus.kstrainer.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class ValidationUtils {

	public static ArrayList<String> validFormats = new ArrayList<>();
	public static ArrayList<String> validDirections = new ArrayList<>();
	public static ArrayList<String> validNeuralNetworks = new ArrayList<>();
	
	public static FileFilter imagesFileFilter = new FileFilter() {
	
		@Override
		public boolean accept(File pathname) {
			if (validFormats.contains(Utils.getFileExtension(pathname))) {
				return true;
			}
			return false;
		}
	};
	
	static {
		validDirections.add("n");
		validDirections.add("ne");
		validDirections.add("e");
		validDirections.add("se");
		validDirections.add("s");
		validDirections.add("sw");
		validDirections.add("w");
		validDirections.add("nw");
	
		validFormats.add("bmp");
		validFormats.add("jpg");
		validFormats.add("png");
		validFormats.add("gif");
	
		validNeuralNetworks.add("all");
		validNeuralNetworks.add("pixels");
		validNeuralNetworks.add("strokes");
	}
}
