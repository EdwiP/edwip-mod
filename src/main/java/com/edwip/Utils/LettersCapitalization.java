package com.edwip.Utils;

public class LettersCapitalization {
    public enum lettersCapitalization {
        NO_CHANGE("No Change"),
        LOWER_ALL("Lower All"),
        UPPER_ALL("Upper All"),
        FIRST_LETTER("First Letter"),
        FIRST_EVERY_LETTER("First Every Letter");

        private final String prefix;

        lettersCapitalization(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public String toString() {
            return prefix;
        }
    }
}
