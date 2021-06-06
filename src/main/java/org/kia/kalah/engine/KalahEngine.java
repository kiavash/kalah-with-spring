package org.kia.kalah.engine;

import org.kia.kalah.dto.BoardConstants;
import org.kia.kalah.dto.KalahBoard;
import org.kia.kalah.dto.Pit;
import org.kia.kalah.exception.KalahEngineException;
import org.springframework.stereotype.Component;

@Component
public class KalahEngine {
    /**
     * Here we try to execute the main action which is choosing a pit and putting it's
     * stones into following pits based on games rules
     * @param board
     * @return true if some stones moved over board otherwise false
     */
    public void moveStones(KalahBoard board) throws KalahEngineException {
        var stonesMoved = false;
        var pitNum = getPitNum(board.getPlayers().getPlayerNumber(), board.getPlayers().getSelectedMove());
        var stones = board.getPits()[pitNum].removeStones();
        if(stones == 0)
            throw new KalahEngineException("Pit has no stone!");
        while (stones != 0)
        {
            pitNum++;
            if (pitNum > (BoardConstants.TOTAL_PLAYING_PITs.getValue() -1))
                pitNum = 0;
            if (!isOtherPlayerKalah(board,pitNum))
            {
                board.getPits()[pitNum].addStones(1);
                stones--;
                stonesMoved = true;
            }
        }
        if(stonesMoved && board.getPits()[pitNum].getStones()==1 && !isKalah(pitNum) && isPitForMe(board,pitNum)){
            stones = board.getPits()[getOppositePitNum(pitNum)].removeStones();
            if(stones>0){
                stones += board.getPits()[pitNum].removeStones();
                board.getPits()[getPlayerKalah(board)].addStones(stones);
            }
        }
        if (stonesMoved && !isKalah(pitNum))
            board.getPlayers().changePlayer();
        if(!stonesMoved)
            throw new KalahEngineException("No Stone Moved!");
        if(isGameOver(board)){
            board.setGameOver(true);
            stones = 0;
            for (int i = BoardConstants.KALAH1.getValue()+1; i<(BoardConstants.PLAYING_PITs.getValue() +1); i++) {
                stones += board.getPits()[i].removeStones();
            }
            board.getPits()[BoardConstants.KALAH2.getValue()].addStones(stones);
            stones = 0;
            for (int i = BoardConstants.KALAH2.getValue()+1; i< BoardConstants.TOTAL_PLAYING_PITs.getValue(); i++) {
                stones += board.getPits()[i].removeStones();
            }
            board.getPits()[BoardConstants.KALAH1.getValue()].addStones(stones);
        }
    }
    private boolean isGameOver(KalahBoard board){
        Pit[] pits = board.getPits();
        boolean firstPlayerHasStone = false;
        boolean secondPlayerHasStone = false;
        for (int i = BoardConstants.KALAH1.getValue()+1; i<(BoardConstants.PLAYING_PITs.getValue() +1); i++) {
            if(pits[i].getStones()>0) {
                firstPlayerHasStone = true;
                break;
            }
        }
        for (int i = BoardConstants.KALAH2.getValue()+1; i< BoardConstants.TOTAL_PLAYING_PITs.getValue(); i++) {
            if(pits[i].getStones()>0) {
                secondPlayerHasStone = true;
                break;
            }
        }
        return !(firstPlayerHasStone && secondPlayerHasStone);
    }
    private int getPlayerKalah(KalahBoard board){
        if(board.getPlayers().getPlayerNumber()== BoardConstants.FIRST_PLAYER.getValue())
            return BoardConstants.KALAH2.getValue();
        else
            return BoardConstants.KALAH1.getValue();
    }
    private boolean isOtherPlayerKalah(KalahBoard board, int pitNum){
        return
                (board.getPlayers().getPlayerNumber()== BoardConstants.FIRST_PLAYER.getValue() && pitNum==BoardConstants.KALAH1.getValue())
                ||
                (board.getPlayers().getPlayerNumber()== BoardConstants.SECOND_PLAYER.getValue() && pitNum==BoardConstants.KALAH2.getValue())
                ;
    }
    private boolean isKalah(int pitNum){
        return pitNum == BoardConstants.KALAH1.getValue() || pitNum == BoardConstants.KALAH2.getValue();
    }
    private boolean isPitForMe(KalahBoard board,int pitNum){
        int currentSide = pitNum / (BoardConstants.PLAYING_PITs.getValue() + 1);
        return board.getPlayers().getPlayerNumber() == currentSide;
    }
    private int getOppositePitNum(int pitNum){
        return BoardConstants.TOTAL_PLAYING_PITs.getValue() - pitNum;
    }

    /**
     * First Player number is 0 Second Player number is 1
     * so we may have correct pit num for player 1 if we add number of playing pits and Kalah to it's pit
     *                      06  05  04  03  02  01
     * Player 1 Kalah    07                        14  Player 2 Kalah
     *                      08  09  10  11  12  13
     * @param playerNumber
     * @param pitNum
     * @return appropriate pit number for calculation
     */
    private int getPitNum(int playerNumber, int pitNum) throws KalahEngineException {
        if(pitNum<1||pitNum==BoardConstants.KALAH2.getValue()||pitNum>=BoardConstants.TOTAL_PLAYING_PITs.getValue())
            throw new KalahEngineException("PitId is not in Range!");
        if(pitNum>(BoardConstants.PLAYING_PITs.getValue()+1))
            return pitNum;
        else
            return playerNumber * (BoardConstants.PLAYING_PITs.getValue() + 1) + pitNum;
    }

}
