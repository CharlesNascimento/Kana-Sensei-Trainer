package org.kansus.ocr;

import java.awt.image.BufferedImage;

/**
 * Interface for listening action that happen during a character extraction
 * process.
 * 
 * @author Charles Nascimento
 */
public interface CharacterExtractionListener {

	/**
	 * Invoked when the character extraction progress advances.
	 * 
	 * @param message The message describing the progress.
	 * @param progress The current progress, ranging from 0 to 100.
	 */
	public void onCharacterExtractionProgressChanged(String message, int progress);

	/**
	 * Invoked when a new character is extracted from the input image.
	 * 
	 * @param image The image only with the extracted character.
	 */
	public void onCharacterExtracted(BufferedImage image);

	/**
	 * Invoked when the character extraction process finishes.
	 */
	public void onCharacterExtractionCompleted();
}
