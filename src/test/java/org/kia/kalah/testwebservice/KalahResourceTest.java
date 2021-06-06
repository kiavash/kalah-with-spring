package org.kia.kalah.testwebservice;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kia.kalah.webservice.KalahResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class KalahResourceTest {
    @Autowired
    private MockMvc mockMvc;
    private Integer gameId;
    @BeforeEach
    public void beforeEach() throws Exception{
        if(gameId == null){
            MvcResult result =
            mockMvc.perform(MockMvcRequestBuilders.post("/games"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("id").exists())
            .andReturn();
            gameId = JsonPath.parse(result.getResponse().getContentAsString()).read("id");
        }
    }
    @Test
    public void testCreateNewGame() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/games"))
                .andExpect(status().isCreated())
        ;
    }
    @Test
    public void testAddPlayer() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.put("/games/add/"+gameId+"/player/Kiavash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("players.0").value("Kiavash"))
                .andExpect(jsonPath("playerNumber").value(0))
                ;
    }
//
//
//    @Test
//    public void testGetBoardStatus(){
//        given()
//                .when().get("/games/"+gameId+"/boardStatus")
//                .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .assertThat()
//                .body("id",equalTo(gameId))
//                .body("uri",notNullValue())
//                .body("status.1",equalTo(6))
//                .body("status.2",equalTo(6))
//                .body("status.3",equalTo(6))
//                .body("status.4",equalTo(6))
//                .body("status.5",equalTo(6))
//                .body("status.6",equalTo(6))
//                .body("status.7",equalTo(0))//Kalah
//                .body("status.8",equalTo(6))
//                .body("status.9",equalTo(6))
//                .body("status.10",equalTo(6))
//                .body("status.11",equalTo(6))
//                .body("status.12",equalTo(6))
//                .body("status.13",equalTo(6))
//                .body("status.14",equalTo(0))
//                .body("playerNumber",equalTo(0))
//                .body("players.0",equalTo(""))
//                .body("players.1",equalTo(""))
//                .body("isGameOver",equalTo(false))
//        ;
//    }
//
//
//    @Test
//    public void testPlay(){
//        given()
//                .when().put("/games/"+gameId+"/pits/1")
//                .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .assertThat()
//                .body("id",equalTo(gameId))
//                .body("uri",notNullValue())
//                .body("status.1",equalTo(0))
//                .body("status.2",equalTo(7))
//                .body("status.3",equalTo(7))
//                .body("status.4",equalTo(7))
//                .body("status.5",equalTo(7))
//                .body("status.6",equalTo(7))
//                .body("status.7",equalTo(1))//Kalah
//                .body("status.8",equalTo(6))
//                .body("status.9",equalTo(6))
//                .body("status.10",equalTo(6))
//                .body("status.11",equalTo(6))
//                .body("status.12",equalTo(6))
//                .body("status.13",equalTo(6))
//                .body("status.14",equalTo(0))
//                .body("playerNumber",equalTo(0))
//                .body("players.0",equalTo(""))
//                .body("players.1",equalTo(""))
//                .body("isGameOver",equalTo(false))
//        ;
//    }
//
    @Test
    public void testPlayWithWrongPitId() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.put("/games/"+gameId+"/pits/15"))
                .andExpect(status().isBadRequest());
//                .assertThat()
//                .body("error",equalTo("PitId is not in Range!"))
        ;
    }
//
//    @Test
//    public void testPlayWithWrongGameId(){
//        given()
//                .when().put("/games/-1/pits/1")
//                .then()
//                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
//                .assertThat()
//                .body("error",equalTo("Game Id not found!"))
//        ;
//    }
//
//    @Test
//    public void testGetGames() {
//        given()
//                .when().get("/games/list")
//                .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .assertThat()
//                .body("games.0",notNullValue());
//        ;
//    }
//    @Test
//    public void testGetPlayers() {
//        given()
//                .when().post("/games/"+gameId+"/players")
//                .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .assertThat()
//                .body("players",notNullValue());
//        ;
//    }
//    @Test
//    public void testGetPlayersWithWrongGameId(){
//        given()
//                .when().post("/games/-1/players")
//                .then()
//                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
//                .assertThat()
//                .body("error",equalTo("Game Id not found!"))
//        ;
//    }


}
