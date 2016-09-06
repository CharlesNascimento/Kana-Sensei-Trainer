package com.kansus.kstrainer;

import java.io.File;
import java.util.ArrayList;

public class TrainingConfig {

	private File pixelsWeightsFile;
	
	private File strokesWeightsFile;
	
	private ArrayList<TrainingInput> inputs;

	public TrainingConfig(File pixelsWeightsFile, File strokesWeightsFile, ArrayList<TrainingInput> inputs) {
		this.pixelsWeightsFile = pixelsWeightsFile;
		this.strokesWeightsFile = strokesWeightsFile;
		this.inputs = inputs;
	}

	public File getPixelsWeightsFile() {
		return pixelsWeightsFile;
	}
	
	public File getStrokesWeightsFile() {
		return strokesWeightsFile;
	}

	public ArrayList<TrainingInput> getInputs() {
		return inputs;
	}
}
