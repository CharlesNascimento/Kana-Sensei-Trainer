package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluateSimilaritiesCommand implements Command {

    private String[] args;
    private CharacterRepository characterRepository;

    public EvaluateSimilaritiesCommand(String[] args) {
        this.args = args;
        characterRepository = new CharacterRepository();
    }

    @Override
    public void execute() {
        Test test = Workspace.getInstance().getCurrentProject().getTestNamed(args[1]);
        List<SimilarityResult> results = evaluateCosineSimilarity(test, false);

        logResults(results);
    }

    private void logResults(List<SimilarityResult> results) {
        try {
            File projectRoot = Workspace.getInstance().getCurrentProject().getRootDirectory();
            Log.setFile(new File(projectRoot, "similarity_evaluation" + ".txt"));

            for (SimilarityResult result : results) {
                Log.write("Sample: " + result.sampleFile.getName() + " (" + result.sampleCharacter.getKana() + ")");
                Log.writeln(" x Reference: " + result.referenceFile.getName() + " (" + result.referenceCharacter.getKana() + ")");
                Log.writeln("Cosine Similarity: " + result.similarity);
                Log.writeln("Rating: " + Utils.rangeToRange(result.similarity, -1, 1, 0, 10));
                Log.newLine();
            }

            Log.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SimilarityResult {

        private File referenceFile;
        private Character referenceCharacter;
        private File sampleFile;
        private Character sampleCharacter;
        private double similarity;

        SimilarityResult(File referenceFile, Character referenceCharacter,
                         File sampleFile, Character sampleCharacter, double similarity) {
            this.referenceFile = referenceFile;
            this.referenceCharacter = referenceCharacter;
            this.sampleFile = sampleFile;
            this.sampleCharacter = sampleCharacter;
            this.similarity = similarity;
        }
    }

    private List<SimilarityResult> evaluateCosineSimilarity(Test test, boolean fullMode) {
        List<SimilarityResult> results = new ArrayList<>();

        Project project = Workspace.getInstance().getCurrentProject();
        File[] referenceImages = new File(project.getInputDirectory(), "Default").listFiles(ValidationUtils.imagesFileFilter);
        File[] samplesDirImages = test.getInputDirectory().listFiles(ValidationUtils.imagesFileFilter);

        if (referenceImages == null || samplesDirImages == null) {
            return results;
        }

        for (File sampleFile : samplesDirImages) {
            try {
                String sampleName = FileUtils.getFilenameWithoutExtension(sampleFile);
                int sampleCharId = Integer.parseInt(sampleName.split("-")[0]);
                Character sampleCharacter = characterRepository.getById(sampleCharId);

                BufferedImage bi = ImageIO.read(sampleFile);
                double[] anorm = PreNetworkUtils.normalizePixels(bi, false, false);

                for (File referenceFile : referenceImages) {
                    String referenceName = FileUtils.getFilenameWithoutExtension(referenceFile);
                    int refCharId = Integer.parseInt(referenceName.split("-")[0]);

                    if (sampleCharId != refCharId && !fullMode) {
                        continue;
                    }

                    Character referenceCharacter = characterRepository.getById(refCharId);
                    BufferedImage reference = ImageIO.read(referenceFile);
                    double[] refNorm = PreNetworkUtils.normalizePixels(reference, false, false);
                    double sim = Utils.cosineSimilarity(refNorm, anorm);

                    SimilarityResult result = new SimilarityResult(referenceFile, referenceCharacter, sampleFile,
                            sampleCharacter, sim);
                    results.add(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    @Override
    public boolean validate() {
        return false;
    }
}
