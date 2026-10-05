package laplateforme.pigeon.boitenoire.generator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import laplateforme.pigeon.boitenoire.models.LogEvent;
import laplateforme.pigeon.boitenoire.models.enums.EventTypes;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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
        Random random = new Random();
        List<LogEvent> query = new ArrayList<>();
        int totalEvents = 100_000;
        int batchSize = 5_000;

        for (int i = 0; i < totalEvents; i++) {
            query.add(generateEvent(random));

            if (query.size() >= batchSize) {
                mongoTemplate.insertAll(query);
                query.clear();

                System.out.println((i + 1) + " / " + totalEvents + "Generate Logs");
            }
        }
    }



        // Users
        private String generateUser (Random random) {
            int luck = random.nextInt(100);

            // 70 %
            if (luck < 70) {
                int id = 1 + random.nextInt(20);
                return "user_"+id;
            }

            // 20%
            else if (luck < 90) {
                int id = 21 + random.nextInt(500);
                return "user_"+id;
            }

            // 10%
            else {
                int id = 521 + random.nextInt(5000);
                return "user_"+id;
            }
        }



        // Time
        private Instant Time (Random random) {

            // Day
            int day = 1 + random.nextInt(365);
            LocalDate date = LocalDate.ofYearDay(2025, day);

            // Weekend
            if (date.getDayOfWeek().getValue() >= 6 && random.nextInt(100) < 80) {
                day = 1 + random.nextInt(365);
                date = LocalDate.ofYearDay(2025, day);
            }

            // Hour
            int hour;
                if (random.nextInt(100) < 95 ) {
                    hour = 8 + random.nextInt(12);
                }
                else {
                    hour = random.nextInt(24);
                }

            // Minute & Second
            int minute = random.nextInt(60);
            int second = random.nextInt(60);

            LocalDateTime dateTime = date.atTime(hour, minute, second);

            return dateTime.toInstant(ZoneOffset.UTC);
        }





        private LogEvent generateEvent (Random random) {
            String userId = generateUser(random);
            Instant timestamp = Time(random);
            EventTypes eventType;
            Map<String, Object> subObjects = new HashMap<>();
            int randomiser = random.nextInt(100);



            // Request
            if ( randomiser < 70) {
                eventType = EventTypes.REQUEST;
                subObjects.put("endpoint", "/api/messages");
                subObjects.put("method", "GET");
                subObjects.put("statusCode", 200);
                subObjects.put("responsesTimeMs", 40 + random.nextInt(75));
            }


            //  Notification
            else if (randomiser < 85) {
                eventType = EventTypes.NOTIFICATION;
                subObjects.put("channel", "PUSH");
                subObjects.put("status", "DELIVERED");
            }


            // Connect User
            else if (randomiser < 95) {
                eventType = EventTypes.LOGIN;
                subObjects.put("method", "PASSWORD");
                subObjects.put("success", true);
            }


            // Error
            else if (randomiser < 98) {
                eventType = EventTypes.ERROR;
                subObjects.put("errorType", "TimeoutException");
                subObjects.put("message", "request timed out...");
            }


            // Payment
            else {
                eventType = EventTypes.PAYMENT;
                subObjects.put("amount", 6.54);
                subObjects.put("currency", "EUR");
                subObjects.put("status", "SUCCESS");

            }
            return new LogEvent(null, userId, eventType, timestamp, subObjects);
            }
    }