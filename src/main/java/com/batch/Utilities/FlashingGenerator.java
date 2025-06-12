//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch.Utilities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FlashingGenerator implements Runnable {
    private BooleanProperty flasher = new SimpleBooleanProperty();
    private static volatile FlashingGenerator singelton = null;

    public static FlashingGenerator getSystem() {
        synchronized(FlashingGenerator.class) {
            if (singelton == null) {
                singelton = new FlashingGenerator();
            }
        }

        return singelton;
    }

    public void run() {
        if (this.flasher.getValue().equals(true)) {
            this.flasher.setValue(Boolean.FALSE);
        } else {
            this.flasher.setValue(Boolean.TRUE);
        }

    }

    public BooleanProperty getFlasher() {
        return this.flasher;
    }
}
