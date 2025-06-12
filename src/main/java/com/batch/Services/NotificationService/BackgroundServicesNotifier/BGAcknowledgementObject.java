
package com.batch.Services.NotificationService.BackgroundServicesNotifier;

import com.batch.Services.NotificationService.AcknowledgementObject;

public class BGAcknowledgementObject implements AcknowledgementObject {
    private final String serviceName;
    private final String familyName;

    public String toString() {
        return "Back ground Acknowledgement Object -->  serviceName = %-20s  familyName=%-50s ".formatted(this.serviceName, this.familyName);
    }

    public BGAcknowledgementObject(final String serviceName, final String familyName) {
        this.serviceName = serviceName;
        this.familyName = familyName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BGAcknowledgementObject)) {
            return false;
        } else {
            BGAcknowledgementObject other = (BGAcknowledgementObject)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$serviceName = this.getServiceName();
                Object other$serviceName = other.getServiceName();
                if (this$serviceName == null) {
                    if (other$serviceName != null) {
                        return false;
                    }
                } else if (!this$serviceName.equals(other$serviceName)) {
                    return false;
                }

                Object this$familyName = this.getFamilyName();
                Object other$familyName = other.getFamilyName();
                if (this$familyName == null) {
                    if (other$familyName != null) {
                        return false;
                    }
                } else if (!this$familyName.equals(other$familyName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BGAcknowledgementObject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $serviceName = this.getServiceName();
        result = result * 59 + ($serviceName == null ? 43 : $serviceName.hashCode());
        Object $familyName = this.getFamilyName();
        result = result * 59 + ($familyName == null ? 43 : $familyName.hashCode());
        return result;
    }
}
