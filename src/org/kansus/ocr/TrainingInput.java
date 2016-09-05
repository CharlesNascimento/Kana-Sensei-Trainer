package org.kansus.ocr;

import java.io.File;

public class TrainingInput {

	public boolean saveExtractedCharactersToFile;
	public String alphabet;
	public String charFamily;
	public File inputImage;

	public TrainingInput(boolean saveExtractedCharactersToFile, String alphabet, String charFamily, File inputImage) {
		this.saveExtractedCharactersToFile = saveExtractedCharactersToFile;
		this.alphabet = alphabet;
		this.charFamily = charFamily;
		this.inputImage = inputImage;
	}

	public boolean isSaveExtractedCharactersToFile() {
		return saveExtractedCharactersToFile;
	}

	public String getAlphabet() {
		return alphabet;
	}

	public String getCharFamily() {
		return charFamily;
	}

	public File getInputImage() {
		return inputImage;
	}
}