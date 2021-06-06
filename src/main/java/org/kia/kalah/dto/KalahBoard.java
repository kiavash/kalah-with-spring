package org.kia.kalah.dto;

import lombok.Data;

import java.util.Arrays;

/**
 * This class is representing main game board
 */

/**
 * @Data is lambok annotation for generating getters and setters at compile time
 */
@Data
public class KalahBoard {
    private Pit[] pits = new Pit[BoardConstants.TOTAL_PLAYING_PITs.getValue()];
    private PlayerInfo players;
    private boolean isGameOver = false;


    public KalahBoard() {
        for (int i=0;i< pits.length;i++)
            pits[i] = new Pit();
    }

    /**
     * Initial the board with 4 stones in each pit
     */
    public void initBoard(){
        for (int i=0;i< pits.length;i++) {
            if(i != BoardConstants.KALAH1.getValue() && i!= BoardConstants.KALAH2.getValue())
                pits[i].setStones(BoardConstants.STARTING_STONES.getValue());
        }
    }


    @Override
    public String toString() {
        return "KalahBoard{" +
                "pits=" + Arrays.deepToString(pits) + "\nplayers=" + players.toString()+
                '}';
    }
}
