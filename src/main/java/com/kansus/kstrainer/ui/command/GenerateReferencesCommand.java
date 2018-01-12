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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

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
                //byte[] compressed = compressor.compress(bytes);
                byte[] compressed = compress(binaryImage);
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

    public byte[] compress(BufferedImage image) {
        try {
            float quality = .5f;

            // get all image writers for JPG format
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            //File compressedImageFile = new File("bosta.png");
            //OutputStream os = new FileOutputStream(compressedImageFile);


            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            // compress to a given quality
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            // appends a complete image stream containing a single image and
            //associated stream and image metadata and thumbnails to the output

            writer.write(null, new IIOImage(image, null, null), param);

            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    class JPEGCompressor implements Compressor {


        @Override
        public byte[] compress(byte[] data) {

            return new byte[0];
        }

        @Override
        public String compress(String string) {
            return null;
        }

        @Override
        public byte[] decompress(byte[] data) throws DataFormatException {
            return new byte[0];
        }
    }

    @Override
    public boolean validate() {
        return false;
    }
}
