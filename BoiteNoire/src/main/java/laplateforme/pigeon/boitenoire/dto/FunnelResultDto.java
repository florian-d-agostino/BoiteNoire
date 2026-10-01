package laplateforme.pigeon.boitenoire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunnelResultDto {
    private String step;
    private long userCount;
    private double conversionRate;
}