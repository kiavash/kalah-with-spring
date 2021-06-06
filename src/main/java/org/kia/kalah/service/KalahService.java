package org.kia.kalah.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.kia.kalah.dto.BoardConstants;
import org.kia.kalah.dto.KalahBoard;
import org.kia.kalah.dto.PlayerInfo;
import org.kia.kalah.engine.KalahEngine;
import org.kia.kalah.exception.KalahEngineException;
import org.kia.kalah.exception.KalahServiceException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Game Service Manager in order to execute games for lots of different players
 */

/**
 *is a Lombok-provided annotation that, at compilation time will automatically generate an SLF4J
 * (Simple Logging Facade for Java, https://www.slf4j.org/) Logger static property in the class.
 *
 */
@Slf4j
/**
 * We mark beans with @Service to indicate that they're holding the business logic
 */
@Service
public class KalahService {
    private Map<Integer, KalahBoard> boardsMap = new HashMap<>();
    private final KalahEngine kalahEngine;
    private final ObjectMapper objectMapper;

    public KalahService(KalahEngine kalahEngine, ObjectMapper objectMapper) {
        this.kalahEngine = kalahEngine;
        this.objectMapper = objectMapper;
    }


    /**
     * Creating New Game!
     * @return Created Game Id
     */
    public Integer createNewGame(){
        KalahBoard kalahBoard = new KalahBoard();
        kalahBoard.initBoard();
        PlayerInfo players = new PlayerInfo();
        Random random = new Random();
        players.setGameId(random.nextInt(Integer.MAX_VALUE)); //Generate Not Negative random id
        players.setPlayerNumber(BoardConstants.FIRST_PLAYER.getValue());
        kalahBoard.setPlayers(players);
        boardsMap.put(players.getGameId(),kalahBoard);
        return players.getGameId();
    }

    /**
     * Assign player Name to the board
     * @param gameId
     * @param playerName
     * @return true if we could successfully assign player name otherwise false
     */
    public void addPlayer(Integer gameId,String playerName) throws KalahServiceException {
        PlayerInfo playerInfo = getBoardStatus(gameId).getPlayers();
        if(playerInfo.getPlayer1().isEmpty())
            playerInfo.setPlayer1(playerName);
        else if(playerInfo.getPlayer2().isEmpty())
            playerInfo.setPlayer2(playerName);
    }

    public KalahBoard play(Integer gameId,Integer selectedMove) throws KalahServiceException, KalahEngineException {
        KalahBoard kalahBoard = getBoardStatus(gameId);
        kalahBoard.getPlayers().setSelectedMove(selectedMove);
        kalahEngine.moveStones(kalahBoard);
        log.debug(kalahBoard.toString());
        return kalahBoard;
    }

    public ObjectNode getGames(){
        ObjectNode jsonObjectBuilder = objectMapper.createObjectNode();
        AtomicInteger i = new AtomicInteger();
        boardsMap.entrySet().stream()
                .filter(e->e.getValue().getPlayers().isWaitingForPlayer())
                .map(Map.Entry::getKey)
                .sorted()
                .forEach(e->jsonObjectBuilder.put(Integer.toString(i.getAndIncrement()),e));
        return jsonObjectBuilder;
    }

    public KalahBoard getBoardStatus(Integer gameId) throws KalahServiceException {
        return  Optional.ofNullable(boardsMap.get(gameId)).orElseThrow(()->new KalahServiceException("Game Id not found!"));
    }

}
