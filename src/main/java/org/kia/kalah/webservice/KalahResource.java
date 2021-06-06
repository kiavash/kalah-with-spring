package org.kia.kalah.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.kia.kalah.dto.BoardConstants;
import org.kia.kalah.dto.KalahBoard;
import org.kia.kalah.dto.Pit;
import org.kia.kalah.exception.KalahEngineException;
import org.kia.kalah.exception.KalahServiceException;
import org.kia.kalah.service.KalahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *is a Lombok-provided annotation that, at compilation time will automatically generate an SLF4J
 * (Simple Logging Facade for Java, https://www.slf4j.org/) Logger static property in the class.
 *
 */
@Slf4j
/**
 * The @RestController annotation serves two purposes.
 * First, it’s a stereotype annotation like @Controller and @Service that marks a class for discovery by component scanning.
 * But most relevant to the discussion of REST, the @RestController annotation tells Spring that all handler methods
 * in the controller should have their return value written directly to the body of the response, rather than being carried
 * in the model to a view for rendering.
 */
@RestController
/**
 * This specifies that any of the handler methods in KalahResource
 * will only handle requests if the request’s Accept header includes “application/json”.
 */
@RequestMapping(path = "/",produces = "application/json")
/**
 * Because the Angular portion of the application will be running on a separate host and/or port from the API
 * (at least for now), the web browser will prevent your Angular client from consuming the API. This restriction can be
 * overcome by including CORS (Cross-Origin Resource Sharing) headers in the server responses. Spring makes it easy to
 * apply CORS with the @CrossOrigin annotation. As applied here, @CrossOrigin allows clients from any domain to consume
 * the API.
 */
@CrossOrigin(origins = "*")
public class KalahResource {
    @Autowired
    private KalahService kalahService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ObjectMapper mapper;


    @PostMapping(path = "/games")
    @ResponseStatus(HttpStatus.CREATED)
    public ObjectNode createNew() {
        Integer gameId = kalahService.createNewGame();
        ObjectNode response = mapper.createObjectNode();
        response.put("id", gameId)
                .put("url", request.getRequestURL() + "games/" + gameId);
        return response;
    }


    @PutMapping(path = "/games/add/{gameId}/player/{playerName}")
    public ObjectNode addPlayer(
            @PathVariable("gameId") Integer gameId,
            @PathVariable("playerName") String playerName
    ) {
        try {
            kalahService.addPlayer(gameId,playerName);
            KalahBoard board = kalahService.getBoardStatus(gameId);
            ObjectNode players = getPlayers(board);
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.set("players",players);
            objectNode.put("playerNumber", board.getPlayers().getPlayerNumber());
            return objectNode;
        } catch (KalahServiceException e) {
            return handleBadRequest(e);
        }
    }

    @PutMapping(path = "/games/{gameId}/pits/{pitId}")
    public ObjectNode play(
            @PathVariable("gameId") Integer gameId,
            @PathVariable("pitId") Integer selectedMove
    ) {
        try {
            KalahBoard kalahBoard = kalahService.play(gameId,selectedMove);
            return getBoardStatusJsonObject(kalahBoard);
        } catch (KalahServiceException | KalahEngineException exception) {
            return handleBadRequest(exception);
        }
    }

    @GetMapping(path = "/games/{gameId}/boardStatus")
    public ObjectNode getBoardStatus(
            @PathVariable("gameId") Integer gameId
    ) {
        try {
            KalahBoard board = kalahService.getBoardStatus(gameId);
            return getBoardStatusJsonObject(board);
        } catch (KalahServiceException exception) {
            return handleBadRequest(exception);
        }
    }

    @GetMapping(path = "/games/list")
    public ObjectNode getGames() {
        ObjectNode jsonObjectBuilder = kalahService.getGames();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.set("games", jsonObjectBuilder);
        return objectNode;
    }

    @PostMapping(path = "/games/{gameId}/players")
    public ObjectNode getPlayers(
            @PathVariable("gameId") Integer gameId
    ) {
        try {
            KalahBoard board = kalahService.getBoardStatus(gameId);
            ObjectNode jsonObjectPlayers = getPlayers(board);
            ObjectNode responseObject = mapper.createObjectNode();
            responseObject.set("players", jsonObjectPlayers);
            return responseObject;
        } catch (KalahServiceException exception) {
            return handleBadRequest(exception);
        }
    }


    private ObjectNode getBoardStatusJsonObject(KalahBoard board){
        ObjectNode jsonObjectBoard = boardToJson(board);
        ObjectNode jsonObjectResponse = mapper.createObjectNode();
        if(board!=null){
            jsonObjectResponse
                    .put("id", board.getPlayers().getGameId())
                    .put("uri", request.getRequestURL() + "games/" + board.getPlayers().getGameId())
                    .put("playerNumber", board.getPlayers().getPlayerNumber())
                    .put("isGameOver", board.isGameOver());
                    jsonObjectResponse.set("status", jsonObjectBoard);
                    jsonObjectResponse.set("players", getPlayers(board));

        }
        return jsonObjectResponse;
    }

    private ObjectNode boardToJson(KalahBoard kalahBoard) {
        ObjectNode objectNode = mapper.createObjectNode();
        if(kalahBoard != null){
            Pit[] pits = kalahBoard.getPits();
            for (int i = 1; i<BoardConstants.TOTAL_PLAYING_PITs.getValue(); i++) {
                objectNode.put(Integer.toString(i),pits[i].getStones());
            }
            /*
             * Just Consider pit 0 as 14 in output!!
             */
            objectNode.put(Integer.toString(BoardConstants.TOTAL_PLAYING_PITs.getValue()),pits[0].getStones());
        }
        return objectNode;
    }

    private ObjectNode getPlayers(KalahBoard board){
        ObjectNode objectNode = mapper.createObjectNode();
        if(board != null){
            objectNode.put("1",board.getPlayers().getPlayer2());
            objectNode.put("0",board.getPlayers().getPlayer1());
        }
        return objectNode;
    }
    private ObjectNode handleBadRequest(Exception exception){
        log.error(exception.getMessage());
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("error",exception.getMessage());
        return objectNode;
    }
}
