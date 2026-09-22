package com.ceiromania.pathfinder.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Sends HTTP requests through the full Spring stack: routing, param binding, JSON, exception handler. */
@SpringBootTest
@AutoConfigureMockMvc
class HeuristicSearchControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void zerindToBucharestOverHttp() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Zerind").param("end", "Bucharest"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.distance").value(493))
                .andExpect(jsonPath("$.totalNodes").value(10))
                .andExpect(jsonPath("$.path", contains(
                        "Zerind", "Arad", "Sibiu", "Rimnicu Vilcea", "Pitesti", "Bucharest")))
                .andExpect(jsonPath("$.routes['1'][0].town").value("Arad"))
                // pins the JSON key spelling the frontend will see
                .andExpect(jsonPath("$.routes['1'][0].gn").value(75))
                .andExpect(jsonPath("$.routes['1'][0].hn").value(453))
                .andExpect(jsonPath("$.routes['1'][0].fn").value(528))
                .andExpect(jsonPath("$.routes['1'][0].expandedAt").value(1))
                .andExpect(jsonPath("$.routes['0'][*].town", hasItem("Oradea")))
                .andExpect(jsonPath("$.routes['5'][0].town").value("Bucharest"))
                .andExpect(jsonPath("$.runtime").isNumber());
    }

    @Test
    void startEqualsEndOverHttp() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Arad").param("end", "Arad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(0))
                .andExpect(jsonPath("$.path", contains("Arad")))
                .andExpect(jsonPath("$.routes['0'][0].expandedAt").value(0));
    }

    @Test
    void unknownStartCityIs404() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Nowhere").param("end", "Bucharest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Start city not found: Nowhere"));
    }

    @Test
    void unknownEndCityIs404() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Arad").param("end", "bucharest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("End city not found: bucharest"));
    }

    @Test
    void missingParamIs400() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Arad"))
                .andExpect(status().isBadRequest());
    }
}
