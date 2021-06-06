package org.kia.kalah.dto;

import lombok.Data;

import java.util.Objects;

/**
 * This class keeps the player and game info
 */

/**
 * @Data is lambok annotation for generating getters and setters at compile time
 */

@Data
public class PlayerInfo {
    private String player1="";
    private String player2="";
    private Integer gameId;
    private Integer playerNumber;
    private Integer selectedMove;


    public void changePlayer(){
        playerNumber = (playerNumber==BoardConstants.FIRST_PLAYER.getValue()) ? BoardConstants.SECOND_PLAYER.getValue() : BoardConstants.FIRST_PLAYER.getValue();
    }

    public boolean isWaitingForPlayer(){
        return player1.isEmpty() || player2.isEmpty();
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", gameId=" + gameId +
                ", playerNumber=" + playerNumber +
                ", selectedMove=" + selectedMove +
                '}';
    }
}
