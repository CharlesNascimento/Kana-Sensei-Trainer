package com.kansus.kstrainer.ui;

import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.ui.command.Command;

import java.io.File;

/**
 * Main class of the application.
 *
 * @author Charles Nascimento
 */
public class Main {

    /**
     * Entry point of the application. The following commands and arguments are
     * supported:<br>
     * <br>
     * <b>train &ltneural-network&gt &ltconfig-file&gt</b><br>
     * <b>evaluate pixels &ltweights-file&gt &ltcharacter-file&gt</b><br>
     * <b>evaluate strokes &ltweights-file&gt&ltstrokes&gt</b><br>
     * <b>evaluate-network pixels &lnetwork-config-file&gt</b><br>
     * <br>
     * Examples:<br>
     * <br>
     * <b>train pixels config.json</b><br>
     * <b>train strokes config.json</b><br>
     * <b>evaluate pixels pixels.mlp test.jpg</b><br>
     * <b>evaluate strokes strokes.mlp e,s,s</b>
     * <b>evaluate-network pixels Default</b><br>
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        Workspace workspace = Workspace.getInstance();
        workspace.setRootDirectory(new File("E:\\KST\\KST-24"));
        workspace.setCurrentProjectByName("PX-N");

        CommandFactory commandFactory = new CommandFactory();
        Command command = commandFactory.create(args);
        command.execute();
    }
}