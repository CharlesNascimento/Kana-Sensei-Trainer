package com.kansus.kmlp.core;

import com.kansus.kmlp.activation.*;

public class ActivationFunctionInjector {

    private ActivationFunction current = new DefaultActivationFunction();

    public ActivationFunction getCurrent() {
        return current;
    }

    public void setCurrent(ActivationFunction current) {
        this.current = current;
    }
}
