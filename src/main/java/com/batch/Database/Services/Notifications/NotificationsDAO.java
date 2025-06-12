package com.batch.Database.Services.Notifications;

import com.batch.Database.Entities.Notifications.NotificationDTO;
import com.batch.Database.Repositories.Notifactions.NotificationRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationsDAO {
    @Autowired
    NotificationRepository notificationRepository;

    @Async
    public String createTable() {
        return this.notificationRepository.createTable();
    }

    @Async
    public CompletableFuture<NotificationDTO> save(NotificationDTO entity) {
        return CompletableFuture.completedFuture(this.notificationRepository.save(entity));
    }

    @Async
    public CompletableFuture<List<NotificationDTO>> saveAll(Iterable<NotificationDTO> entities) {
        return CompletableFuture.completedFuture(Lists.newArrayList(this.notificationRepository.saveAll(entities)));
    }

    @Async
    public CompletableFuture<NotificationDTO> findById(Long along) {
        return CompletableFuture.completedFuture(this.notificationRepository.findById(along).get());
    }

    @Async
    public CompletableFuture<Boolean> existsById(Long along) {
        return CompletableFuture.completedFuture(this.notificationRepository.existsById(along));
    }

    @Async
    public CompletableFuture<List<NotificationDTO>> findAll() {
        return CompletableFuture.completedFuture(Lists.newArrayList(this.notificationRepository.findAll()));
    }

    public List<NotificationDTO> findAllSync() {
        return Lists.newArrayList(this.notificationRepository.findAll());
    }

    @Async
    public void deleteById(Long along) {
        this.notificationRepository.deleteById(along);
    }

    @Async
    public void delete(NotificationDTO entity) {
        this.notificationRepository.delete(entity);
    }

    @Async
    public void deleteAll(Iterable<? extends NotificationDTO> entities) {
        this.notificationRepository.deleteAll(entities);
    }

    @Async
    public void deleteAll() {
        this.notificationRepository.deleteAll();
    }

    @Async
    public void deleteSelected(String serviceName, String familyName) {
        this.notificationRepository.deleteSelected(serviceName, familyName);
    }
}
