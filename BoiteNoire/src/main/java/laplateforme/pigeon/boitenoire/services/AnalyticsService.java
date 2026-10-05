package laplateforme.pigeon.boitenoire.services;

import laplateforme.pigeon.boitenoire.dto.EndpointStatsDto;
import laplateforme.pigeon.boitenoire.dto.ErrorDistributionDto;
import laplateforme.pigeon.boitenoire.dto.FunnelResultDto;
import laplateforme.pigeon.boitenoire.dto.TopUserDto;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public AnalyticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // 1. Top 10 most active users within a given date range
    public List<TopUserDto> getTopActiveUsers(Instant startDate, Instant endDate) {
        // Filter by date range
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("timestamp").gte(startDate).lte(endDate)
        );

        // Group by user and count events
        GroupOperation groupStage = Aggregation.group("userId")
            .count().as("totalEvents");

        // Sort by total events descending
        SortOperation sortStage = Aggregation.sort(Sort.Direction.DESC, "totalEvents");

        // Keep top 10
        LimitOperation limitStage = Aggregation.limit(10);

        // Project _id to userId
        ProjectionOperation projectStage = Aggregation.project("totalEvents")
            .and("_id").as("userId");

        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            groupStage,
            sortStage,
            limitStage,
            projectStage
        );

        AggregationResults<TopUserDto> results = mongoTemplate.aggregate(
            aggregation,
            "logs",
            TopUserDto.class
        );

        return results.getMappedResults();
    }

    // 2. Error distribution by type and day
    public List<ErrorDistributionDto> getErrorDistribution(Instant startDate, Instant endDate) {
        // Filter error events in date range
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("eventType").is("ERROR")
                .and("timestamp").gte(startDate).lte(endDate)
        );

        // Format timestamp as YYYY-MM-DD and extract error type
        ProjectionOperation projectDateAndType = Aggregation.project()
            .and(DateOperators.DateToString.dateOf("timestamp").toString("%Y-%m-%d")).as("date")
            .and("subObjects.errorType").as("errorType");

        // Group by date and error type
        GroupOperation groupStage = Aggregation.group("date", "errorType")
            .count().as("count");

        // Sort chronologically by date
        SortOperation sortStage = Aggregation.sort(
            Sort.Direction.ASC, "_id.date"
        ).and(Sort.Direction.DESC, "count");

        // Map _id fields to output DTO
        ProjectionOperation projectOutput = Aggregation.project("count")
            .and("_id.date").as("date")
            .and("_id.errorType").as("errorType");

        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            projectDateAndType,
            groupStage,
            sortStage,
            projectOutput
        );

        AggregationResults<ErrorDistributionDto> results = mongoTemplate.aggregate(
            aggregation,
            "logs",
            ErrorDistributionDto.class
        );

        return results.getMappedResults();
    }

    // 3. Response time per endpoint: average and 95th percentile
    public List<EndpointStatsDto> getEndpointStats() {
        // Filter API request events with latency
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("eventType").is("REQUEST")
                .and("subObjects.endpoint").ne(null)
                .and("subObjects.timeResponse").ne(null)
        );

        // Sort latencies ascending for percentile lookup
        SortOperation sortStage = Aggregation.sort(Sort.Direction.ASC, "subObjects.timeResponse");

        // Group by endpoint, calculate average and collect latencies
        GroupOperation groupStage = Aggregation.group("subObjects.endpoint")
            .avg("subObjects.timeResponse").as("avgResponseTime")
            .push("subObjects.timeResponse").as("times")
            .count().as("total");

        // Pick 95th percentile value from sorted latencies
        AggregationOperation projectStage = context -> new Document("$project",
            new Document("endpoint", "$_id")
                .append("avgResponseTime", new Document("$round", List.of("$avgResponseTime", 2)))
                .append("p95ResponseTime", new Document("$arrayElemAt", List.of(
                    "$times",
                    new Document("$min", List.of(
                        new Document("$floor", new Document("$multiply", List.of(0.95, "$total"))),
                        new Document("$subtract", List.of("$total", 1))
                    ))
                )))
        );

        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            sortStage,
            groupStage,
            projectStage
        );

        AggregationResults<EndpointStatsDto> results = mongoTemplate.aggregate(
            aggregation,
            "logs",
            EndpointStatsDto.class
        );

        return results.getMappedResults();
    }

    // 4. Conversion funnel: LOGIN -> REQUEST -> PAYMENT
    public List<FunnelResultDto> getConversionFunnel() {
        // Match funnel events
        MatchOperation matchStage = Aggregation.match(
            Criteria.where("eventType").in("LOGIN", "REQUEST", "PAYMENT")
        );

        // Group by user and resolve step timestamps
        GroupOperation groupByUser = Aggregation.group("userId")
            .min(AggregationExpression.from(
                ConditionalOperators.when(Criteria.where("eventType").is("LOGIN"))
                    .thenValueOf("timestamp")
                    .otherwise((Object) null)
            )).as("loginTime")
            .max(AggregationExpression.from(
                ConditionalOperators.when(Criteria.where("eventType").is("REQUEST"))
                    .thenValueOf("timestamp")
                    .otherwise((Object) null)
            )).as("requestTime")
            .max(AggregationExpression.from(
                ConditionalOperators.when(Criteria.where("eventType").is("PAYMENT"))
                    .thenValueOf("timestamp")
                    .otherwise((Object) null)
            )).as("paymentTime");

        // Count unique users who completed each sequential step
        AggregationOperation countStepsStage = context -> new Document("$group",
            new Document("_id", null)
                .append("step1", new Document("$sum", new Document("$cond", List.of(
                    new Document("$ne", List.of("$loginTime", null)), 1, 0
                ))))
                .append("step2", new Document("$sum", new Document("$cond", List.of(
                    new Document("$and", List.of(
                        new Document("$ne", List.of("$loginTime", null)),
                        new Document("$ne", List.of("$requestTime", null)),
                        new Document("$gte", List.of("$requestTime", "$loginTime"))
                    )), 1, 0
                ))))
                .append("step3", new Document("$sum", new Document("$cond", List.of(
                    new Document("$and", List.of(
                        new Document("$ne", List.of("$loginTime", null)),
                        new Document("$ne", List.of("$requestTime", null)),
                        new Document("$ne", List.of("$paymentTime", null)),
                        new Document("$gte", List.of("$requestTime", "$loginTime")),
                        new Document("$gte", List.of("$paymentTime", "$requestTime"))
                    )), 1, 0
                ))))
        );

        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            groupByUser,
            countStepsStage
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation,
            "logs",
            Document.class
        );

        Document doc = results.getUniqueMappedResult();

        long s1 = doc != null && doc.get("step1") != null ? ((Number) doc.get("step1")).longValue() : 0L;
        long s2 = doc != null && doc.get("step2") != null ? ((Number) doc.get("step2")).longValue() : 0L;
        long s3 = doc != null && doc.get("step3") != null ? ((Number) doc.get("step3")).longValue() : 0L;

        // Compute conversion percentages
        double rate1 = s1 > 0 ? 100.0 : 0.0;
        double rate2 = s1 > 0 ? Math.round(((double) s2 / s1 * 100.0) * 100.0) / 100.0 : 0.0;
        double rate3 = s1 > 0 ? Math.round(((double) s3 / s1 * 100.0) * 100.0) / 100.0 : 0.0;

        return List.of(
            new FunnelResultDto("LOGIN", s1, rate1),
            new FunnelResultDto("REQUEST", s2, rate2),
            new FunnelResultDto("PAYMENT", s3, rate3)
        );
    }
}