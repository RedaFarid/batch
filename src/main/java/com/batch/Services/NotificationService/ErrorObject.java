package com.batch.Services.NotificationService;

import com.google.common.base.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorObject {
    private final ResetEvent resetEvent;
    private final AddErrorEvent addErrorEvent;
    private final ObservableSet<String> errorMessage = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new String[0]));
    private String serviceName;
    private String errorFamily;
    private long errorCount = 0L;
    private LocalDateTime creationDate;

    public ErrorObject(String serviceName, String errorFamily, ResetEvent resetEvent, AddErrorEvent addErrorEvent) {
        this.serviceName = serviceName;
        this.errorFamily = errorFamily;
        this.resetEvent = resetEvent;
        this.addErrorEvent = addErrorEvent;
    }

    public boolean addError(MessageObject error) {
        boolean contains = this.errorMessage.contains(error.getMessage());
        this.errorMessage.add(error.getMessage());
        ++this.errorCount;
        if (!contains && error.isOriginal()) {
            this.addErrorEvent.addErrorMessage(this.serviceName, this.errorFamily, error.getMessage());
        }

        return contains;
    }

    public boolean removeErrorMessage(String message) {
        this.errorMessage.remove(message);
        --this.errorCount;
        if (this.errorMessage.size() == 0) {
            this.errorCount = 0L;
            return true;
        } else {
            return false;
        }
    }

    public ReadOnlyBooleanProperty resetError(AcknowledgementObject acknowledgementObject) {
        return this.resetEvent.reset(acknowledgementObject);
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getErrorFamily() {
        return this.errorFamily;
    }

    public void setErrorFamily(String errorFamily) {
        this.errorFamily = errorFamily;
    }

    public ObservableSet<String> getErrorMessage() {
        return this.errorMessage;
    }

    public List<String> getErrorMessageList() {
        return new ArrayList(this.errorMessage);
    }

    public long getErrorCount() {
        return this.errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ErrorObject that = (ErrorObject) o;
            return this.errorCount == that.errorCount && Objects.equal(this.serviceName, that.serviceName) && Objects.equal(this.errorFamily, that.errorFamily) && Objects.equal(this.errorMessage, that.errorMessage);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.serviceName, this.errorFamily, this.errorMessage, this.errorCount);
    }

    public String toString() {
        return "ErrorObject{serviceName='" + this.serviceName + "', errorFamily='" + this.errorFamily + "', errorMessage=" + this.errorMessage + ", errorCount=" + this.errorCount + "}";
    }
}
