package laplateforme.pigeon.boitenoire.generator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;



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

        private Instant Time (Random random) {

            int day = 1 + random.nextInt(365);
            int hour;
                if (random.nextInt(100) <80 ) {
                    hour = 8 + random.nextInt(12);
                }
                else {
                    hour = random.nextInt(24);
                }

            int minute = random.nextInt(60);
            int second = random.nextInt(60);


            LocalDate date = LocalDate.ofYearDay(2012, day);

            LocalDateTime dateTime = date.atTime(hour, minute, second);

            return dateTime.toInstant(ZoneOffset.UTC);
        }
    }