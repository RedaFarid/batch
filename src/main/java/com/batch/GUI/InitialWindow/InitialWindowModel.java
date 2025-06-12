

package com.batch.GUI.InitialWindow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InitialWindowModel {
    private StringProperty connectionInfo = new SimpleStringProperty();
    private BooleanProperty connectionStatus = new SimpleBooleanProperty();
    private StringProperty airPressureInfo = new SimpleStringProperty();
    private BooleanProperty airPressureStatus = new SimpleBooleanProperty();
    private StringProperty overUnderVoltageInfo = new SimpleStringProperty();
    private BooleanProperty overUnderVoltageStatus = new SimpleBooleanProperty();
    private StringProperty esdInfo = new SimpleStringProperty();
    private BooleanProperty esdStatus = new SimpleBooleanProperty();
    private DoubleProperty gauge1 = new SimpleDoubleProperty();
    private DoubleProperty gauge2 = new SimpleDoubleProperty();

    public StringProperty getConnectionInfo() {
        return this.connectionInfo;
    }

    public BooleanProperty getConnectionStatus() {
        return this.connectionStatus;
    }

    public StringProperty getAirPressureInfo() {
        return this.airPressureInfo;
    }

    public BooleanProperty getAirPressureStatus() {
        return this.airPressureStatus;
    }

    public StringProperty getOverUnderVoltageInfo() {
        return this.overUnderVoltageInfo;
    }

    public BooleanProperty getOverUnderVoltageStatus() {
        return this.overUnderVoltageStatus;
    }

    public StringProperty getEsdInfo() {
        return this.esdInfo;
    }

    public BooleanProperty getEsdStatus() {
        return this.esdStatus;
    }

    public DoubleProperty getGauge1() {
        return this.gauge1;
    }

    public DoubleProperty getGauge2() {
        return this.gauge2;
    }

    public void setConnectionInfo(final StringProperty connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public void setConnectionStatus(final BooleanProperty connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public void setAirPressureInfo(final StringProperty airPressureInfo) {
        this.airPressureInfo = airPressureInfo;
    }

    public void setAirPressureStatus(final BooleanProperty airPressureStatus) {
        this.airPressureStatus = airPressureStatus;
    }

    public void setOverUnderVoltageInfo(final StringProperty overUnderVoltageInfo) {
        this.overUnderVoltageInfo = overUnderVoltageInfo;
    }

    public void setOverUnderVoltageStatus(final BooleanProperty overUnderVoltageStatus) {
        this.overUnderVoltageStatus = overUnderVoltageStatus;
    }

    public void setEsdInfo(final StringProperty esdInfo) {
        this.esdInfo = esdInfo;
    }

    public void setEsdStatus(final BooleanProperty esdStatus) {
        this.esdStatus = esdStatus;
    }

    public void setGauge1(final DoubleProperty gauge1) {
        this.gauge1 = gauge1;
    }

    public void setGauge2(final DoubleProperty gauge2) {
        this.gauge2 = gauge2;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof InitialWindowModel)) {
            return false;
        } else {
            InitialWindowModel other = (InitialWindowModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$connectionInfo = this.getConnectionInfo();
                Object other$connectionInfo = other.getConnectionInfo();
                if (this$connectionInfo == null) {
                    if (other$connectionInfo != null) {
                        return false;
                    }
                } else if (!this$connectionInfo.equals(other$connectionInfo)) {
                    return false;
                }

                Object this$connectionStatus = this.getConnectionStatus();
                Object other$connectionStatus = other.getConnectionStatus();
                if (this$connectionStatus == null) {
                    if (other$connectionStatus != null) {
                        return false;
                    }
                } else if (!this$connectionStatus.equals(other$connectionStatus)) {
                    return false;
                }

                Object this$airPressureInfo = this.getAirPressureInfo();
                Object other$airPressureInfo = other.getAirPressureInfo();
                if (this$airPressureInfo == null) {
                    if (other$airPressureInfo != null) {
                        return false;
                    }
                } else if (!this$airPressureInfo.equals(other$airPressureInfo)) {
                    return false;
                }

                Object this$airPressureStatus = this.getAirPressureStatus();
                Object other$airPressureStatus = other.getAirPressureStatus();
                if (this$airPressureStatus == null) {
                    if (other$airPressureStatus != null) {
                        return false;
                    }
                } else if (!this$airPressureStatus.equals(other$airPressureStatus)) {
                    return false;
                }

                Object this$overUnderVoltageInfo = this.getOverUnderVoltageInfo();
                Object other$overUnderVoltageInfo = other.getOverUnderVoltageInfo();
                if (this$overUnderVoltageInfo == null) {
                    if (other$overUnderVoltageInfo != null) {
                        return false;
                    }
                } else if (!this$overUnderVoltageInfo.equals(other$overUnderVoltageInfo)) {
                    return false;
                }

                Object this$overUnderVoltageStatus = this.getOverUnderVoltageStatus();
                Object other$overUnderVoltageStatus = other.getOverUnderVoltageStatus();
                if (this$overUnderVoltageStatus == null) {
                    if (other$overUnderVoltageStatus != null) {
                        return false;
                    }
                } else if (!this$overUnderVoltageStatus.equals(other$overUnderVoltageStatus)) {
                    return false;
                }

                Object this$esdInfo = this.getEsdInfo();
                Object other$esdInfo = other.getEsdInfo();
                if (this$esdInfo == null) {
                    if (other$esdInfo != null) {
                        return false;
                    }
                } else if (!this$esdInfo.equals(other$esdInfo)) {
                    return false;
                }

                Object this$esdStatus = this.getEsdStatus();
                Object other$esdStatus = other.getEsdStatus();
                if (this$esdStatus == null) {
                    if (other$esdStatus != null) {
                        return false;
                    }
                } else if (!this$esdStatus.equals(other$esdStatus)) {
                    return false;
                }

                Object this$gauge1 = this.getGauge1();
                Object other$gauge1 = other.getGauge1();
                if (this$gauge1 == null) {
                    if (other$gauge1 != null) {
                        return false;
                    }
                } else if (!this$gauge1.equals(other$gauge1)) {
                    return false;
                }

                Object this$gauge2 = this.getGauge2();
                Object other$gauge2 = other.getGauge2();
                if (this$gauge2 == null) {
                    if (other$gauge2 != null) {
                        return false;
                    }
                } else if (!this$gauge2.equals(other$gauge2)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof InitialWindowModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $connectionInfo = this.getConnectionInfo();
        result = result * 59 + ($connectionInfo == null ? 43 : $connectionInfo.hashCode());
        Object $connectionStatus = this.getConnectionStatus();
        result = result * 59 + ($connectionStatus == null ? 43 : $connectionStatus.hashCode());
        Object $airPressureInfo = this.getAirPressureInfo();
        result = result * 59 + ($airPressureInfo == null ? 43 : $airPressureInfo.hashCode());
        Object $airPressureStatus = this.getAirPressureStatus();
        result = result * 59 + ($airPressureStatus == null ? 43 : $airPressureStatus.hashCode());
        Object $overUnderVoltageInfo = this.getOverUnderVoltageInfo();
        result = result * 59 + ($overUnderVoltageInfo == null ? 43 : $overUnderVoltageInfo.hashCode());
        Object $overUnderVoltageStatus = this.getOverUnderVoltageStatus();
        result = result * 59 + ($overUnderVoltageStatus == null ? 43 : $overUnderVoltageStatus.hashCode());
        Object $esdInfo = this.getEsdInfo();
        result = result * 59 + ($esdInfo == null ? 43 : $esdInfo.hashCode());
        Object $esdStatus = this.getEsdStatus();
        result = result * 59 + ($esdStatus == null ? 43 : $esdStatus.hashCode());
        Object $gauge1 = this.getGauge1();
        result = result * 59 + ($gauge1 == null ? 43 : $gauge1.hashCode());
        Object $gauge2 = this.getGauge2();
        result = result * 59 + ($gauge2 == null ? 43 : $gauge2.hashCode());
        return result;
    }

    public String toString() {
        StringProperty var10000 = this.getConnectionInfo();
        return "InitialWindowModel(connectionInfo=" + var10000 + ", connectionStatus=" + this.getConnectionStatus() + ", airPressureInfo=" + this.getAirPressureInfo() + ", airPressureStatus=" + this.getAirPressureStatus() + ", overUnderVoltageInfo=" + this.getOverUnderVoltageInfo() + ", overUnderVoltageStatus=" + this.getOverUnderVoltageStatus() + ", esdInfo=" + this.getEsdInfo() + ", esdStatus=" + this.getEsdStatus() + ", gauge1=" + this.getGauge1() + ", gauge2=" + this.getGauge2() + ")";
    }
}
