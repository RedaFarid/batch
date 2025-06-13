package com.batch.Services.NotificationService.BackgroundServicesNotifier;

import com.batch.Database.Entities.Notifications.NotificationDTO;
import com.batch.Database.Services.Notifications.NotificationsDAO;
import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.NotificationService.*;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Service
@BackGroundServices
public class BackGroundNotificationsService extends NotificationService {
    private final AtomicBoolean isConfigurationsEnabled = new AtomicBoolean(true);
    @Autowired
    private NotificationsDAO notificationsDAO;
    @Autowired
    private Executor executor;

    @Autowired
    private MessageLoggingService log;

    @Async
    public void saveNewErrorToDatabase(String serviceName, String familyName, String message) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setNotificationService("Background Service");
        notificationDTO.setServiceName(serviceName);
        notificationDTO.setFamilyName(familyName);
        notificationDTO.setErrorMessage(message);
        this.notificationsDAO.save(notificationDTO).exceptionally((error) -> {
            log.logExcption("BackgroundnotificationService [saveNewErrorToDatabase]", error);
            return null;
        });
    }

    public ReadOnlyBooleanProperty removeErrorFromDatabase(AcknowledgementObject acknowledgementObject) {
        Task<Boolean> resetTask = this.resetTask(acknowledgementObject);
        this.executor.execute(resetTask);
        return resetTask.runningProperty();
    }

    private Task<Boolean> resetTask(final AcknowledgementObject acknowledgementObject) {
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {
                try {
                    BGAcknowledgementObject acknowledgementObject1 = (BGAcknowledgementObject) acknowledgementObject;
                    BackGroundNotificationsService.this.notificationsDAO.deleteSelected(acknowledgementObject1.getServiceName(), acknowledgementObject1.getFamilyName());
                } catch (Exception e) {
                    log.logExcption("BackgroundnotificationService [saveNewErrorToDatabase]", e);
                }

                return null;
            }
        };
    }

    @Scheduled(
            fixedDelay = 2500L,
            initialDelay = 5000L
    )
    public void updateDataStructureFromDatabase() {
        try {
            if (this.isConfigurationsEnabled.get()) {
                List<NotificationDTO> databaseList = this.notificationsDAO.findAllSync();
                List<ErrorObject> actualList = this.getNotificationsDataStructure().getAllErrorObjects();
                databaseList.stream().map(CompareObject::toCompareObject).filter((item) -> {
                    Stream<CompareObject> list = actualList.stream().flatMap(CompareObject::toCompareObject);
                    Objects.requireNonNull(item);
                    return list.noneMatch(item::equals);
                }).forEach((newAdd) -> this.addErrorMessage(newAdd.getServiceName(), newAdd.getFamilyName(), new MessageObject(newAdd.getErrorMessage(), false)));
                actualList.stream().flatMap(CompareObject::toCompareObject).filter((item) -> databaseList.stream().map(CompareObject::toCompareObject).noneMatch((dataBaseItem) -> dataBaseItem.equals(item))).forEach((remove) -> this.removeErrorMessage(remove.getServiceName(), remove.getFamilyName(), remove.getErrorMessage()));
            }
        } catch (Exception e) {
            log.logExcption("BackgroundnotificationService [updateDataStructureFromDatabase]", e);
        }

    }

    @EventListener
    public void atInit(ContextRefreshedEvent event) {
//        this.notificationsDAO.createTable();
    }

    public void errorsListenerToGenerateAlarms() {
    }

    @Getter
    private static class CompareObject {
        private String serviceName;
        private String familyName;
        private String errorMessage;

        public CompareObject(String serviceName, String familyName, String errorMessage) {
            this.serviceName = serviceName;
            this.familyName = familyName;
            this.errorMessage = errorMessage;
        }

        public static Stream<CompareObject> toCompareObject(ErrorObject errorObject) {
            return errorObject.getErrorMessageList().stream().map((item) -> new CompareObject(errorObject.getServiceName(), errorObject.getErrorFamily(), item));
        }

        public static CompareObject toCompareObject(NotificationDTO notificationDTO) {
            return new CompareObject(notificationDTO.getServiceName(), notificationDTO.getFamilyName(), notificationDTO.getErrorMessage());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                CompareObject that = (CompareObject) o;
                return com.google.common.base.Objects.equal(this.serviceName, that.serviceName) && com.google.common.base.Objects.equal(this.familyName, that.familyName) && com.google.common.base.Objects.equal(this.errorMessage, that.errorMessage);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return com.google.common.base.Objects.hashCode(this.serviceName, this.familyName, this.errorMessage);
        }

        public void setServiceName(final String serviceName) {
            this.serviceName = serviceName;
        }

        public void setFamilyName(final String familyName) {
            this.familyName = familyName;
        }

        public void setErrorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String toString() {
            String var10000 = this.getServiceName();
            return "BackGroundNotificationsService.CompareObject(serviceName=" + var10000 + ", familyName=" + this.getFamilyName() + ", errorMessage=" + this.getErrorMessage() + ")";
        }
    }
}
