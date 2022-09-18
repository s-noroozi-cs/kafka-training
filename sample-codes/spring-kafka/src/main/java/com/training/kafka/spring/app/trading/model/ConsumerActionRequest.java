package com.training.kafka.spring.app.trading.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsumerActionRequest {
    private Action action;
    private String offsetDateTime;

    public boolean hasValidAction() {
        for (Action act : Action.values()) {
            if (act.equals(action))
                return true;
        }
        return false;
    }

    public LocalDateTime getOffsetDateTimeAsLocalDateTime(){
        return LocalDateTime.parse(offsetDateTime);
    }
}
