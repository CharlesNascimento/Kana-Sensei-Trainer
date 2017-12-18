package com.kansus.kstrainer;

import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class EvaluateSimpleCommand implements Command {

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
}
