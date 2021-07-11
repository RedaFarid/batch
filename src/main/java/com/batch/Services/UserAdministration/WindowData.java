package com.batch.Services.UserAdministration;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindowData {
    private String windowName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowData that = (WindowData) o;
        return Objects.equal(windowName, that.windowName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(windowName);
    }

    @Override
    public String toString() {
        return windowName;
    }
}
