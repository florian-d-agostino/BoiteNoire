package laplateforme.pigeon.boitenoire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointStatsDto {
    private String endpoint;
    private double avgResponseTime;
    private double p95ResponseTime;
}