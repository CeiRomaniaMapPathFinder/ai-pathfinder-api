package com.ceiromania.pathfinder.controllers;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** memoryUsageKb over HTTP: present and positive on both endpoints, and stable when the same query repeats. */
@SpringBootTest
@AutoConfigureMockMvc
class MemoryUsageTest {

    private static final int RUNS = 5;
    private static final double MAX_SPREAD = 0.10;

    @Autowired
    private MockMvc mvc;

    @Test
    void blindSearchReportsMemoryUsage() throws Exception {
        mvc.perform(get("/api/blind-search").param("start", "Arad").param("end", "Bucharest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memoryUsageKb", greaterThan(0.0)));
    }

    @Test
    void heuristicSearchReportsMemoryUsage() throws Exception {
        mvc.perform(get("/api/heuristic-search").param("start", "Arad").param("end", "Bucharest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memoryUsageKb", greaterThan(0.0)));
    }

    @Test
    void blindSearchMemoryIsStableAcrossRuns() throws Exception {
        assertStableAcrossRuns("/api/blind-search");
    }

    @Test
    void heuristicSearchMemoryIsStableAcrossRuns() throws Exception {
        assertStableAcrossRuns("/api/heuristic-search");
    }

    private void assertStableAcrossRuns(String url) throws Exception {
        double min = Double.MAX_VALUE;
        double max = 0;
        for (int i = 0; i < RUNS; i++) {
            String body = mvc.perform(get(url).param("start", "Arad").param("end", "Bucharest"))
                    .andReturn().getResponse().getContentAsString();
            double kb = ((Number) JsonPath.read(body, "$.memoryUsageKb")).doubleValue();
            min = Math.min(min, kb);
            max = Math.max(max, kb);
        }
        double spread = (max - min) / min;
        assertTrue(spread <= MAX_SPREAD, url + " spread " + spread + " (min " + min + " KB, max " + max + " KB)");
    }
}
