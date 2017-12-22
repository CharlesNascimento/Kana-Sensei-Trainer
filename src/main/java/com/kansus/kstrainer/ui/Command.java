package com.kansus.kstrainer.ui;

interface Command {

    void execute();

    boolean validate();
}
