package com.batch.Services.NotificationService;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface ResetEvent {
    ReadOnlyBooleanProperty reset(AcknowledgementObject acknowledgementObject);
}
