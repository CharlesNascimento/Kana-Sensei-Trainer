package org.kansus.ocr;

import java.io.File;
import java.util.ArrayList;

public class TrainingConfig {

	private File weightsFile;
	private ArrayList<TrainingInput> inputs;

	public TrainingConfig(File weightsFile, ArrayList<TrainingInput> inputs) {
		this.weightsFile = weightsFile;
		this.inputs = inputs;
	}

	public File getWeightsFile() {
		return weightsFile;
	}

	public ArrayList<TrainingInput> getInputs() {
		return inputs;
	}
}
