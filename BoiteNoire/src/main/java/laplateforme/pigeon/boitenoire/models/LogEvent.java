package laplateforme.pigeon.boitenoire.models;



import laplateforme.pigeon.boitenoire.models.enums.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import java.time.Instant;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "logs")



public class LogEvent {
    @Id
    private String id;
    private String userId;
    private EventTypes eventType;
    private Instant timestamp;
    private Map<String,Object> subObjects;
}