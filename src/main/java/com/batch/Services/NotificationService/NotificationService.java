

package com.batch.Services.NotificationService;

import com.batch.Utilities.StringUtilsL;
import javafx.beans.property.ReadOnlyBooleanProperty;
import org.springframework.scheduling.annotation.Async;

public abstract class NotificationService {
    protected final NotificationsDataStructure notificationsDataStructure = new NotificationsDataStructure();
    protected final AddErrorEvent addErrorEvent = this::saveNewErrorToDatabase;
    protected final ResetEvent resetEvent = this::removeErrorFromDatabase;

    @Async
    public void newErrorMessage(String serviceName, String familyName, String error) {
        this.addErrorMessage(serviceName, familyName, new MessageObject(StringUtilsL.textLimiter(error, 70), true));
    }

    @Async
    public void newErrorMessagePopUpOnly(String serviceName, String familyName, String error) {
        this.notificationsDataStructure.refreshListener(serviceName, familyName, StringUtilsL.textLimiter(error, 70));
    }

    protected void addErrorMessage(String serviceName, String familyName, MessageObject error) {
        this.notificationsDataStructure.addErrorMessage(serviceName, familyName, error, this.addErrorEvent, this.resetEvent);
    }

    protected void removeErrorMessage(String serviceName, String familyName, String error) {
        this.notificationsDataStructure.removeErrorMessage(serviceName, familyName, error);
    }

    protected abstract void saveNewErrorToDatabase(String serviceName, String familyName, String message);

    protected abstract ReadOnlyBooleanProperty removeErrorFromDatabase(AcknowledgementObject acknowledgementObject);

    public abstract void updateDataStructureFromDatabase();

    public abstract void errorsListenerToGenerateAlarms();

    public NotificationsDataStructure getNotificationsDataStructure() {
        return this.notificationsDataStructure;
    }
}
