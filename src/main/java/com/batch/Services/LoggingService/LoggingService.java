
package com.batch.Services.LoggingService;

import com.batch.Database.Entities.Log;
import com.batch.Database.Repositories.LogRepository;
import com.batch.Utilities.LogIdentefires;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LoggingService {

    private static boolean userStatus;
    private long greaterID = 0;

    @Autowired
    private LogRepository logRepository;

    public List<Log> getAllLogs() {
        List<Log> list = logRepository.findAll();
        return list;
    }

    public List<Log> getLogsForAutoUpdateWindow() {
        List<Log> logs = new LinkedList<>();
        try {
            logs = logRepository.getLogsTillID(greaterID);
            long x = logs.isEmpty() ? 0 : logs.get(0).getId();
            greaterID = (x < greaterID) ? greaterID : x;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Async("ServiceExecutor")
    public void LogRecord(Log log) {
        logRepository.save(log);
    }

    @Async("ServiceExecutor")
    public void LogRecordForException(String source, Exception e) {
        try {
            String message = "";
            for (StackTraceElement object : e.getStackTrace()) {
                message += object.toString() + "\n";
            }
            Log log = new Log(LogIdentefires.System.name(), message);
            log.setIdentifier(LogIdentefires.System.name());
            log.setSource(source);

            logRepository.save(log);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
