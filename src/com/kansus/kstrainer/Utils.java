package com.kansus.kstrainer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class with utility methods.
 * 
 * @author Charles Nascimento
 */
public class Utils {

	/**
	 * Loads the file describing all the characters supported by the
	 * application.
	 * 
	 * @return A map containing all the alphabets, their families, the families'
	 *         characters and the characters' strokes.
	 */
	public static HashMap<String, HashMap<String, ArrayList<Character>>> loadCharactersFile() {
		HashMap<String, HashMap<String, ArrayList<Character>>> alphabets = new HashMap<>();

		try {
			JSONParser parser = new JSONParser();
			JSONArray alphabetsArray = (JSONArray) parser.parse(new FileReader("characters.json"));

			for (Object a : alphabetsArray) {
				JSONObject alphabetObject = (JSONObject) a;
				String alphabet = (String) alphabetObject.get("alphabet");

				HashMap<String, ArrayList<Character>> families = new HashMap<>();
				JSONArray familiesArray = (JSONArray) alphabetObject.get("families");
				for (Object f : familiesArray) {
					ArrayList<Character> characters = new ArrayList<>();

					JSONObject familyObject = (JSONObject) f;
					String family = (String) familyObject.get("family");

					JSONArray charactersArray = (JSONArray) familyObject.get("characters");
					for (Object c : charactersArray) {
						JSONObject characterObject = (JSONObject) c;

						int idValue = Integer.parseInt((String) characterObject.get("id"));
						String charValue = (String) characterObject.get("char");
						ArrayList<String> strokes = new ArrayList<>();

						JSONArray strokesArray = (JSONArray) characterObject.get("strokes");
						for (Object s : strokesArray) {
							JSONObject strokeObject = (JSONObject) s;
							strokes.add((String) strokeObject.get("direction"));
						}

						Character character = new Character(charValue, idValue, strokes);
						characters.add(character);
					}

					families.put(family, characters);
				}

				alphabets.put(alphabet, families);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return alphabets;
	}

	/**
	 * Loads the training configuration file.
	 * 
	 * @param trainingConfigFile The training configuration file.
	 * @return All the configurations in the file.
	 */
	public static TrainingConfig loadTrainingConfiguration(File trainingConfigFile) {
		ArrayList<TrainingInput> trainingInputs = new ArrayList<>();
		TrainingConfig trainingConfig = null;

		try {
			JSONParser parser = new JSONParser();
			JSONObject trainingObject = (JSONObject) parser.parse(new FileReader(trainingConfigFile));
			File pixelsWeightsFile = new File((String) trainingObject.get("pixels_weights_file"));
			File strokesWeightsFile = new File((String) trainingObject.get("strokes_weights_file"));
			JSONArray inputsArray = (JSONArray) trainingObject.get("input");

			for (Object t : inputsArray) {
				JSONObject inputObject = (JSONObject) t;

				boolean saveExtractedCharacters = Boolean
				        .valueOf((String) inputObject.get("save_extracted_characters"));
				File inputImage = new File((String) inputObject.get("path"));
				String alphabet = (String) inputObject.get("alphabet");
				String charFamily = (String) inputObject.get("char_family");

				TrainingInput tc = new TrainingInput(saveExtractedCharacters, alphabet, charFamily, inputImage);

				trainingInputs.add(tc);
			}

			trainingConfig = new TrainingConfig(pixelsWeightsFile, strokesWeightsFile, trainingInputs);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return trainingConfig;
	}

	/**
	 * Loads the training configuration file.
	 * 
	 * @param strokePatternFiles The training configuration file.
	 * @return All the configurations in the file.
	 */
	public static HashMap<String, int[]> loadStrokePatterns() {
		HashMap<String, int[]> strokePatterns = new HashMap<>();

		try {
			JSONParser parser = new JSONParser();
			JSONArray strokePatternsArray = (JSONArray) parser.parse(new FileReader(new File("stroke_patterns.json")));

			for (Object spo : strokePatternsArray) {
				JSONObject strokePatternsObject = (JSONObject) spo;

				String pattern = (String) strokePatternsObject.get("pattern");
				int[] normalization = normalizationStringToArray((String) strokePatternsObject.get("normalization"));

				strokePatterns.put(pattern, normalization);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return strokePatterns;
	}

	private static int[] normalizationStringToArray(String normalizationString) {
		String[] splitedValues = normalizationString.split(",");
		int[] array = new int[splitedValues.length];

		for (int i = 0; i < splitedValues.length; i++) {
			array[i] = Integer.parseInt(splitedValues[i]);
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	public static void savePatternsToFile(HashMap<String, int[]> patterns) {
		JSONArray patternsJsonArray = new JSONArray();
		int id = 0;

		for (Map.Entry<String, int[]> entry : patterns.entrySet()) {
			JSONObject obj = new JSONObject();
			String entryKey = entry.getKey().replace("[", "").replace("]", "");
			int[] entryValue = entry.getValue();
			String normalization = "";

			for (int i = 0; i < entryValue.length; i++) {
				normalization += entryValue[i];

				if (i < entryValue.length - 1) {
					normalization += ",";
				}
			}

			obj.put("id", id++);
			obj.put("pattern", entryKey);
			obj.put("normalization", normalization);
			patternsJsonArray.add(obj);
		}

		try {
			FileWriter file = new FileWriter("stroke_patterns.json");
			file.write(patternsJsonArray.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
