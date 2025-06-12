package com.batch.Services.LoggingService;

import com.batch.Database.Entities.Log;
import com.batch.Database.Entities.User;
import com.batch.Database.Repositories.LogRepository;
import com.batch.Services.UserAdministration.UserEvent;
import com.batch.Services.UserAdministration.UserEventMessage;
import com.batch.Utilities.LogIdentefires;
import com.google.common.collect.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LoggingService {
    private final LogRepository logRepository;
    private long greaterID = 0L;
    private Log lastLog;
    private User currentUser = new User("System");

    public LoggingService(final LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<Log> getAllLogs() {
        return Lists.newArrayList(this.logRepository.findAll());
    }

    public List<Log> getLogsForAutoUpdateWindow() {
        List<Log> logs = new LinkedList();

        try {
            logs = this.logRepository.getLogsTillID(this.greaterID);
            long x = logs.isEmpty() ? 0L : logs.get(0).getId();
            this.greaterID = Math.max(x, this.greaterID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }

    @Async
    public void LogRecord(Log log) {
        log.setUserName(this.currentUser.getUserName());
        log.setGroupName(this.currentUser.getGroup() == null ? "" : this.currentUser.getGroup());
        this.logRepository.save(log);
    }

    @Async
    public void LogRecordForException(String source, Exception e) {
        try {
            StringBuilder message = new StringBuilder();

            for (StackTraceElement object : e.getStackTrace()) {
                message.append(object.toString()).append("\n");
            }

            Log log = new Log(LogIdentefires.System.name(), message.toString());
            log.setIdentifier(LogIdentefires.System.name());
            log.setSource(source);
            this.logRepository.save(log);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Log getLastEnteredLog() {
        if (this.lastLog == null) {
            this.lastLog = this.logRepository.findLast().orElse(new Log());
        }

        return this.lastLog;
    }

    @EventListener
    public void newUserLogIn(UserEvent event) {
        UserEventMessage message = event.getMessage();
        this.currentUser = message.getUser();
    }
}
