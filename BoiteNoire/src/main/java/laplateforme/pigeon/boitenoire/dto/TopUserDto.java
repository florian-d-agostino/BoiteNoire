package laplateforme.pigeon.boitenoire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUserDto {
    private String userId;
    private long totalEvents;
}