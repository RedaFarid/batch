
package com.batch.Services.NotificationService;

public interface AddErrorEvent {
    void addErrorMessage(String serviceName, String familyName, String message);
}
