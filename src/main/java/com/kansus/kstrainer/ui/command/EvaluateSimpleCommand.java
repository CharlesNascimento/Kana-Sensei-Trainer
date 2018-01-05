package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EvaluateSimpleCommand implements Command {

    private String[] args;

    public EvaluateSimpleCommand(String[] args) {
        this.args = args;
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        if (args[1].equals("pixels")) {
            File pixelsWightsFile = new File(args[2]);
            File characterFile = new File(args[3]);

            try {
                NeuralNetworkFacade trainer = new NeuralNetworkFacade();
                BufferedImage characterImage = ImageIO.read(characterFile);
                trainer.evaluate(pixelsWightsFile, characterImage, true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File strokesWeightsFile = new File(args[2]);
            List<String> strokes = Arrays.asList(args[3].split(","));
            NeuralNetworkFacade trainer = new NeuralNetworkFacade();
            trainer.evaluate(strokesWeightsFile, strokes);
        }
    }

    @Override
    public boolean validate() {
        if (args.length != 4) {
            System.err.println("<ERROR>   Incorrect number of parameters.");
            System.err.println("<INFO>    Command syntax: evaluate <training-config-file>");
            return false;
        }

        if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
            System.err.println("<ERROR>   Invalid neural network: " + args[1]);
            return false;
        }

        return true;
    }

    private static boolean validateEvaluateArgs(String[] args) {
        if (args.length != 4) {
            System.err.println("<ERROR>   Incorrect number of parameters.");
            System.err.println("<INFO>    Command syntax: evaluate <training-config-file>");
            return false;
        }

        if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
            System.err.println("<ERROR>   Invalid neural network: " + args[1]);
            return false;
        }

        return true;
    }

    private static boolean validateEvaluatePixelsArgs(File pixelsWeightsFile, File characterFile) {
        if (!characterFile.exists()) {
            System.out.println("<ERROR>   Input image file not found.");
            return false;
        }

        if (!Utils.endsWithAny(characterFile.getName(), ValidationUtils.validFormats)) {
            System.err.println("<ERROR>   " + characterFile.getName()
                    + " is not a supported image file. It should be an image in any of these formats:"
                    + " BMP, JPG, PNG or GIF.");
            return false;
        }

        if (!pixelsWeightsFile.exists()) {
            System.out.println("<WARNING> Weights file not found.");
            return false;
        }

        if (!pixelsWeightsFile.getName().endsWith("mlp")) {
            System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
            return false;
        }

        return true;
    }

    private static boolean validateEvaluateStrokesArgs(File strokesWeightsFile, List<String> strokes) {
        if (!strokesWeightsFile.exists()) {
            System.out.println("<WARNING> Weights file not found.");
            return false;
        }

        if (!strokesWeightsFile.getName().endsWith("mlp")) {
            System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
            return false;
        }

        for (String stroke : strokes) {
            if (!Utils.equalsAny(stroke, ValidationUtils.validDirections)) {
                System.err.println("<ERROR>   Invalid stroke direction: " + stroke);
                return false;
            }
        }

        return true;
    }
}
