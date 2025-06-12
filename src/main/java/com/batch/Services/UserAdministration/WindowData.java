package com.batch.Services.UserAdministration;

import com.google.common.base.Objects;

public class WindowData {
    private String windowName;

    public WindowData(final String windowName) {
        this.windowName = windowName;
    }

    public WindowData() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            WindowData that = (WindowData) o;
            return Objects.equal(this.windowName, that.windowName);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.windowName);
    }

    public String toString() {
        return this.windowName;
    }

    public String getWindowName() {
        return this.windowName;
    }

    public void setWindowName(final String windowName) {
        this.windowName = windowName;
    }
}
