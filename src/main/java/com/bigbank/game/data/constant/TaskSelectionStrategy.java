package com.bigbank.game.data.constant;

import lombok.Getter;

@Getter
public enum TaskSelectionStrategy {

    EASIEST_TASK_SELECTION("Easiest task selection strategy enabled"),
    BIGGEST_REWARD_HAVING_TASK_SELECTION("Biggest reward having task selection strategy enabled");

    private final String title;

    TaskSelectionStrategy(final String title) {
        this.title = title;
    }
}
