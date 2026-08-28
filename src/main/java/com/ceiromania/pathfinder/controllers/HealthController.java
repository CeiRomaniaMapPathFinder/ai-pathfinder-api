package com.ceiromania.pathfinder.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final String commit;

    public HealthController(@Value("${app.commit:unknown}") String commit) {
        this.commit = commit;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "commit", commit);
    }
}
