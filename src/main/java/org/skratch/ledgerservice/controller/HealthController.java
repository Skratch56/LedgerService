package org.skratch.ledgerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @Operation(summary = "Health check", description = "Simple health endpoint for monitoring.")
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
