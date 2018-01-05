package com.kansus.kstrainer.ui.command;

import com.kansus.commons.compressing.Compressor;
import com.kansus.commons.compressing.ZLIBCompressor;
import com.kansus.commons.encoding.Base64DataEncoder;
import com.kansus.commons.encoding.DataEncoder;
import com.kansus.kstrainer.data.json.DefaultJSONHandler;
import com.kansus.kstrainer.image.ImageFilter;
import com.kansus.kstrainer.image.ThresholdFilter;
import com.kansus.kstrainer.model.CharacterReferences;
import com.kansus.kstrainer.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateReferencesCommand implements Command {

    private String[] args;

    private ImageFilter threshold = new ThresholdFilter(210);
    private Compressor compressor = new ZLIBCompressor();
    private DataEncoder dataEncoder = new Base64DataEncoder();

    public GenerateReferencesCommand(String[] args) {
        this.args = args;
    }

    @Override
    public void execute() {
        try {
            File inputFolder = new File(args[2]);
            File[] files = inputFolder.listFiles(ValidationUtils.imagesFileFilter);

            if (files == null) {
                return;
            }

            List<File> folderImages = Arrays.asList(files);
            List<CharacterReferences> allReferences = new ArrayList<>();

            for (File file : folderImages) {
                String sampleName = FileUtils.getFilenameWithoutExtension(file);
                int charId = Integer.parseInt(sampleName.split("-")[0]);

                BufferedImage image = ImageIO.read(file);
                BufferedImage binaryImage = threshold.apply(image);

                byte[] bytes = ((DataBufferByte) binaryImage.getRaster().getDataBuffer()).getData();
                byte[] compressed = compressor.compress(bytes);
                String encoded = dataEncoder.encode(compressed);

                List<String> encodedReferences = new ArrayList<>();
                encodedReferences.add(encoded);

                CharacterReferences references = new CharacterReferences(charId, encodedReferences);
                allReferences.add(references);
            }

            new DefaultJSONHandler().saveToFile(allReferences, new File(inputFolder, "references.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validate() {
        return false;
    }
}
