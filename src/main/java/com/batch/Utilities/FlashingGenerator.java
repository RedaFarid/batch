package com.batch.Utilities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FlashingGenerator implements Runnable {

    private BooleanProperty flasher = new SimpleBooleanProperty();
    private static volatile FlashingGenerator singelton = null;

    public FlashingGenerator() {

    }

    public static FlashingGenerator getSystem() {
        synchronized (FlashingGenerator.class) {
            if (singelton == null) {
                singelton = new FlashingGenerator();
            }
        }
        return singelton;
    }

    @Override
    public void run() {
        if (flasher.getValue().equals(true)) {
            flasher.setValue(Boolean.FALSE);

        } else {
            flasher.setValue(Boolean.TRUE);

        }
    }

    public BooleanProperty getFlasher() {
        return flasher;
    }

}
