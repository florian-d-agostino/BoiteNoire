package laplateforme.pigeon.boitenoire.repositories;

import laplateforme.pigeon.boitenoire.models.LogEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<LogEvent, String> {
}