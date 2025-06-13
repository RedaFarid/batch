package com.batch.Services.LoggingService;

import com.batch.Database.Entities.Log;
import com.batch.Database.Entities.User;
import com.batch.Database.Repositories.LogRepository;
import com.batch.Services.UserAdministration.UserEvent;
import com.batch.Services.UserAdministration.UserEventMessage;
import com.batch.Utilities.LogIdentefires;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
public class MessageLoggingService {

    private final LogRepository logRepository;
    private final long greaterID = 0L;
    private User currentUser = new User("System");

    public MessageLoggingService(final LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public LinkedList<Log> getAllLogs() {
        return this.logRepository.findAll();
    }

    @Async
    public void logEvent(Log inLog) {
        inLog.setUserName(this.currentUser.getUserName());
        inLog.setGroupName(this.currentUser.getGroup() == null ? "" : this.currentUser.getGroup());
        this.logRepository.save(inLog);
        log.info(inLog);
    }

    @Async
    public void system(String message) {
        Log inLog = new Log();
        inLog.setEvent(message);
        inLog.setIdentifier(LogIdentefires.System.name());
        inLog.setUserName(this.currentUser.getUserName());
        inLog.setGroupName(this.currentUser.getGroup() == null ? "" : this.currentUser.getGroup());
        this.logRepository.save(inLog);
        log.info(inLog);
    }

    @Async
    @Transactional
    public void logExcption(String source, Throwable e) {
        try {
            Optional<Log> lastBySource = this.logRepository.findLastBySource(source);
            lastBySource.ifPresentOrElse(item -> {
                StringBuilder message = new StringBuilder();
                for (StackTraceElement object : e.getStackTrace()) {
                    message.append(object.toString()).append("\n");
                }
                if (!Objects.equals(item.getEvent(), message.toString())){
                    Log createdLog = new Log(LogIdentefires.System.name(), message.toString());
                    this.logRepository.save(createdLog);
                    log.info(createdLog);
                }
            },() ->{
                StringBuilder message = new StringBuilder();
                for (StackTraceElement object : e.getStackTrace()) {
                    message.append(object.toString()).append("\n");
                }
                Log createdLog = new Log(LogIdentefires.System.name(), message.toString());
                this.logRepository.save(createdLog);
                log.info(createdLog);
            });

        } catch (Exception ex) {
            log.error(e.getStackTrace());
        }
    }

    public Log getLastEnteredLog() {
        return this.logRepository.findLast().orElse(new Log());
    }

    @EventListener
    public void newUserLogIn(UserEvent event) {
        UserEventMessage message = event.getMessage();
        this.currentUser = message.getUser();
    }
}
