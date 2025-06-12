package com.batch.Database.Repositories.Notifactions;

import com.batch.Database.Entities.Notifications.NotificationDTO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationDTO, Long> {
    @Query("declare @return varchar(50);\nset @return = 'Table [Notifications] Already Exists'\nif not exists (select * from sysobjects where name='Notifications' and xtype='U')\nbegin\ncreate table [Notifications](\n[notificationId] int identity(1,1) primary key,\n[notificationService] varchar(MAX) not null ,\n[serviceName] varchar(MAX) not null ,\n[familyName] varchar(MAX) not null ,\n[errorMessage] varchar(MAX) not null ,\n[creationDate] datetime default GETDATE())\nset @return = 'Table [Notifications] Created'\nend\nselect @return;")
    String createTable();

    @Modifying
    @Query("delete from [Notifications] where [serviceName] = :serviceName and [familyName] = :familyName ")
    boolean deleteSelected(String serviceName, String familyName);
}
