

package com.batch.GUI.Alarms;

import com.batch.Database.Entities.Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AlarmsModel {
    private final ObservableList<Log> allAlarmsList = FXCollections.observableArrayList();
    private final BooleanProperty isShown = new SimpleBooleanProperty();
    private final StringProperty airPressureLoAlarm = new SimpleStringProperty();
    private final StringProperty airPressureHiAlarm = new SimpleStringProperty();

    public ObservableList<Log> getAllAlarmsList() {
        return this.allAlarmsList;
    }

    public BooleanProperty getIsShown() {
        return this.isShown;
    }

    public StringProperty getAirPressureLoAlarm() {
        return this.airPressureLoAlarm;
    }

    public StringProperty getAirPressureHiAlarm() {
        return this.airPressureHiAlarm;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AlarmsModel)) {
            return false;
        } else {
            AlarmsModel other = (AlarmsModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$allAlarmsList = this.getAllAlarmsList();
                Object other$allAlarmsList = other.getAllAlarmsList();
                if (this$allAlarmsList == null) {
                    if (other$allAlarmsList != null) {
                        return false;
                    }
                } else if (!this$allAlarmsList.equals(other$allAlarmsList)) {
                    return false;
                }

                Object this$isShown = this.getIsShown();
                Object other$isShown = other.getIsShown();
                if (this$isShown == null) {
                    if (other$isShown != null) {
                        return false;
                    }
                } else if (!this$isShown.equals(other$isShown)) {
                    return false;
                }

                Object this$airPressureLoAlarm = this.getAirPressureLoAlarm();
                Object other$airPressureLoAlarm = other.getAirPressureLoAlarm();
                if (this$airPressureLoAlarm == null) {
                    if (other$airPressureLoAlarm != null) {
                        return false;
                    }
                } else if (!this$airPressureLoAlarm.equals(other$airPressureLoAlarm)) {
                    return false;
                }

                Object this$airPressureHiAlarm = this.getAirPressureHiAlarm();
                Object other$airPressureHiAlarm = other.getAirPressureHiAlarm();
                if (this$airPressureHiAlarm == null) {
                    if (other$airPressureHiAlarm != null) {
                        return false;
                    }
                } else if (!this$airPressureHiAlarm.equals(other$airPressureHiAlarm)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AlarmsModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $allAlarmsList = this.getAllAlarmsList();
        result = result * 59 + ($allAlarmsList == null ? 43 : $allAlarmsList.hashCode());
        Object $isShown = this.getIsShown();
        result = result * 59 + ($isShown == null ? 43 : $isShown.hashCode());
        Object $airPressureLoAlarm = this.getAirPressureLoAlarm();
        result = result * 59 + ($airPressureLoAlarm == null ? 43 : $airPressureLoAlarm.hashCode());
        Object $airPressureHiAlarm = this.getAirPressureHiAlarm();
        result = result * 59 + ($airPressureHiAlarm == null ? 43 : $airPressureHiAlarm.hashCode());
        return result;
    }

    public String toString() {
        ObservableList var10000 = this.getAllAlarmsList();
        return "AlarmsModel(allAlarmsList=" + var10000 + ", isShown=" + this.getIsShown() + ", airPressureLoAlarm=" + this.getAirPressureLoAlarm() + ", airPressureHiAlarm=" + this.getAirPressureHiAlarm() + ")";
    }
}
