package com.bigbank.game.data.constant;

import lombok.Getter;

@Getter
public enum TaskProbability {

        DIFFICULTY_1("Sure thing", 1),
        DIFFICULTY_2("Piece of cake", 1),
        DIFFICULTY_3("Walk in the park", 2),
        DIFFICULTY_4("Quite likely", 2),
        DIFFICULTY_5("Gamble", 3),
        DIFFICULTY_6("Hmmm....", 3),
        DIFFICULTY_7("Risky", 3),
        DIFFICULTY_8("Rather detrimental", 4),
        DIFFICULTY_9("Playing with fire", 4),
        DIFFICULTY_10("Suicide mission", 5),
        UNKNOWN_DIFFICULTY("Unknown", 0);

        private final String message;
        private final int difficulty;

        TaskProbability(final String message, final int difficulty) {
            this.message = message;
            this.difficulty = difficulty;
        }
}
