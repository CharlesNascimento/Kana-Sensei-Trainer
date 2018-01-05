package com.kansus.kstrainer.core;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import static com.kansus.kstrainer.core.Project.CONFIG_FILE_NAME;

public class Workspace {

    private static final String COMPARISON_RESULT_DIRECTORY_NAME = "Comparison";

    private static Workspace instance;

    private File rootDirectory;
    private File comparisonResultDirectory;
    ;

    private HashMap<String, Project> projects = new HashMap<>();

    private Project currentProject;

    private void scanProjects() {
        FileFilter filter = pathname -> pathname.isDirectory() && new File(pathname, CONFIG_FILE_NAME).exists();
        File[] directories = rootDirectory.listFiles(filter);

        if (directories == null) return;

        for (File directory : directories) {
            projects.put(directory.getName(), new Project(directory));
        }
    }

    public static Workspace getInstance() {
        if (instance != null) return instance;

        instance = new Workspace();
        return instance;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public HashMap<String, Project> getProjects() {
        return projects;
    }

    public File getComparisonResultDirectory() {
        return comparisonResultDirectory;
    }

    public void setCurrentProjectByName(String name) {
        currentProject = projects.get(name);
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.comparisonResultDirectory = new File(rootDirectory, COMPARISON_RESULT_DIRECTORY_NAME);
        scanProjects();
    }
}

