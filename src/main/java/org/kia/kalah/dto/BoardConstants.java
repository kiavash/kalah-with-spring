package org.kia.kalah.dto;

public enum BoardConstants {
    PLAYING_PITs(6), TOTAL_PLAYING_PITs(14),
    STARTING_STONES(6), KALAH1(0), KALAH2(7),
    FIRST_PLAYER(0),SECOND_PLAYER(1);
    private int value;

    BoardConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
