package com.batch.Services.UserAdministration;

import org.springframework.context.ApplicationEvent;

public class UserEvent extends ApplicationEvent {
    public UserEvent(UserEventMessage source) {
        super(source);
    }

    public UserEventMessage getMessage() {
        return (UserEventMessage) this.source;
    }
}
