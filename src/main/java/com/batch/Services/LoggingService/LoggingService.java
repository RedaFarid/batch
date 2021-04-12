
package com.batch.Services.LoggingService;

import com.batch.Database.Entities.Log;
import com.batch.Database.Repositories.LogRepository;
import com.batch.Utilities.LogIdentefires;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private long greaterID = 0;
    private final LogRepository logRepository;


    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    public List<Log> getLogsForAutoUpdateWindow() {
        List<Log> logs = new LinkedList<>();
        try {
            logs = logRepository.getLogsTillID(greaterID);
            long x = logs.isEmpty() ? 0 : logs.get(0).getId();
            greaterID = Math.max(x, greaterID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Async
    public void LogRecord(Log log) {
        logRepository.save(log);
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

            logRepository.save(log);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
