package com.batch.Services.UserAdministration;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Group;
import javafx.application.Platform;
import lombok.Data;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.AuditorAware;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
@Data
public class UserAuditor implements AuditorAware<String> {

public static String user;

    @EventListener
    public void newUserLogIn(UserEvent event) {
        UserEventMessage message = event.getMessage();
        if (message.isLoggedOn()) {
            user= message.getUser().getUserName();
        } else {
            user="System";
        }

    }

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(user);
        }catch (Exception e){
            return Optional.of("System");
        }
    }
}
