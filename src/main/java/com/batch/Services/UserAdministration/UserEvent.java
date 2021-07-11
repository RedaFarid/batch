package com.batch.Services.UserAdministration;

import org.springframework.context.ApplicationEvent;

public class UserEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserEvent(UserEventMessage source) {
        super(source);
    }

    public UserEventMessage getMessage(){
        return ((UserEventMessage) source);
    }
}
