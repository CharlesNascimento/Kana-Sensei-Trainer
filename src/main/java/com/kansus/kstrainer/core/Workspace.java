package com.kansus.kstrainer.core;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class Workspace {

    private static Workspace instance;

    private File rootDirectory;

    private HashMap<String, Project> projects = new HashMap<>();

    private Project currentProject;

    private void scanProjects() {
        FileFilter filter = File::isDirectory;
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

    public void setCurrentProjectByName(String name) {
        currentProject = projects.get(name);
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        scanProjects();
    }
}

