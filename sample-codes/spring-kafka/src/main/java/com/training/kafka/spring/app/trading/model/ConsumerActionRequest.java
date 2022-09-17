package com.training.kafka.spring.app.trading.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerActionRequest {
    private Action action;

    public boolean hasValidAction() {
        for (Action act : Action.values()) {
            if (act.equals(action))
                return true;
        }
        return false;
    }
}
