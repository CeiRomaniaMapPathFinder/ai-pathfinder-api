package com.ceiromania.pathfinder.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ComparisonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void measuresBothSearchesInOneCall() throws Exception {
        mvc.perform(get("/api/compare").param("start", "Arad").param("end", "Bucharest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bfs.runtime").value(greaterThan(0.0), Double.class))
                .andExpect(jsonPath("$.bfs.memoryUsageKb").value(greaterThan(0.0), Double.class))
                .andExpect(jsonPath("$.bfs.timedRuns").value(15000))
                .andExpect(jsonPath("$.astar.runtime").value(greaterThan(0.0), Double.class))
                .andExpect(jsonPath("$.astar.memoryUsageKb").value(greaterThan(0.0), Double.class))
                .andExpect(jsonPath("$.astar.timedRuns").value(15000));
    }

    @Test
    void unknownCityIs404() throws Exception {
        mvc.perform(get("/api/compare").param("start", "Nowhere").param("end", "Bucharest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Start city not found: Nowhere"));
    }
}
