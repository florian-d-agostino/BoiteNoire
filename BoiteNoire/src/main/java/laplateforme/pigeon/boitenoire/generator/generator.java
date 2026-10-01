package laplateforme.pigeon.boitenoire.generator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;



import java.util.Random;

@Component


public class generator implements CommandLineRunner {


        // Attributes
        private final MongoTemplate mongoTemplate;


        // Constructor
        public generator (MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }


        // Methods
    @Override
    public void run(String... args) throws Exception {
    }


        private String generateUser (Random random) {
            int luck = random.nextInt(100);
            if (luck < 80) {
                int id = 1 + random.nextInt(20);
                return "user_"+id;
            }
            else {
                int id = 21 + random.nextInt(980);
                return "user_"+id;
            }

        }
    }
}