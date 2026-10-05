package laplateforme.pigeon.boitenoire.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import laplateforme.pigeon.boitenoire.dto.EndpointStatsDto;
import laplateforme.pigeon.boitenoire.dto.ErrorDistributionDto;
import laplateforme.pigeon.boitenoire.dto.FunnelResultDto;
import laplateforme.pigeon.boitenoire.dto.TopUserDto;
import laplateforme.pigeon.boitenoire.services.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Log analytics endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/top-users")
    @Operation(summary = "Top 10 most active users in a date range")
    public List<TopUserDto> getTopUsers(
            @Parameter(description = "Start date (ISO-8601)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO-8601)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return analyticsService.getTopActiveUsers(startDate, endDate);
    }

    @GetMapping("/errors/distribution")
    @Operation(summary = "Daily error distribution by type")
    public List<ErrorDistributionDto> getErrorDistribution(
            @Parameter(description = "Start date (ISO-8601)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO-8601)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return analyticsService.getErrorDistribution(startDate, endDate);
    }

    @GetMapping("/endpoints/response-times")
    @Operation(summary = "Average and 95th percentile response times per endpoint")
    public List<EndpointStatsDto> getEndpointStats() {
        return analyticsService.getEndpointStats();
    }

    @GetMapping("/funnel")
    @Operation(summary = "Sequential conversion funnel (LOGIN -> REQUEST -> PAYMENT)")
    public List<FunnelResultDto> getFunnel() {
        return analyticsService.getConversionFunnel();
    }
}
