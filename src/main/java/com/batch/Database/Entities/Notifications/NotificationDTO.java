
package com.batch.Database.Entities.Notifications;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("Notifications")
public class NotificationDTO implements Comparable<NotificationDTO>, Cloneable, Serializable {
    @Id
    private Long notificationId;
    private String notificationService;
    private String serviceName;
    private String familyName;
    private String errorMessage;
    @CreatedDate
    private LocalDateTime creationDate;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            NotificationDTO that = (NotificationDTO)o;
            return Objects.equal(this.notificationId, that.notificationId) && Objects.equal(this.notificationService, that.notificationService) && Objects.equal(this.serviceName, that.serviceName) && Objects.equal(this.familyName, that.familyName) && Objects.equal(this.errorMessage, that.errorMessage) && Objects.equal(this.creationDate, that.creationDate);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.notificationId, this.notificationService, this.serviceName, this.familyName, this.errorMessage, this.creationDate});
    }

    public String toString() {
        return "NotificationDTO{notificationId=" + this.notificationId + ", notificationService='" + this.notificationService + "', serviceName='" + this.serviceName + "', familyName='" + this.familyName + "', errorMessage='" + this.errorMessage + "', creationDate=" + this.creationDate + "}";
    }

    public int compareTo(NotificationDTO o) {
        return this.getNotificationId() > o.getNotificationId() ? 1 : -1;
    }

    public Long getNotificationId() {
        return this.notificationId;
    }

    public String getNotificationService() {
        return this.notificationService;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setNotificationId(final Long notificationId) {
        this.notificationId = notificationId;
    }

    public void setNotificationService(final String notificationService) {
        this.notificationService = notificationService;
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

    public void setCreationDate(final LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public NotificationDTO() {
    }

    public NotificationDTO(final Long notificationId, final String notificationService, final String serviceName, final String familyName, final String errorMessage, final LocalDateTime creationDate) {
        this.notificationId = notificationId;
        this.notificationService = notificationService;
        this.serviceName = serviceName;
        this.familyName = familyName;
        this.errorMessage = errorMessage;
        this.creationDate = creationDate;
    }
}
