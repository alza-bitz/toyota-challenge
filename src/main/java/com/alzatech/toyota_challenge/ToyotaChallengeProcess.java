package com.alzatech.toyota_challenge;

import com.alzatech.toyota_challenge.dao.DataReader;
import com.alzatech.toyota_challenge.model.TitleRatingRecord;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import static org.apache.spark.sql.functions.*;

import java.util.concurrent.TimeoutException;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.stereotype.Component;

@Component
public class ToyotaChallengeProcess {

    private static final Column RANK = column("numVotes")
            .divide("avg_numVotes").multiply(column("averageRating"));

    private final Dataset<TitleRatingRecord> titleRatings;

    private StreamingQuery query;

    public ToyotaChallengeProcess(DataReader<TitleRatingRecord> titleRatingsReader)
            throws TimeoutException, StreamingQueryException {
        this.titleRatings = titleRatingsReader.readData();
    }

    private Dataset<?> latest() {
        return titleRatings
                .withWatermark("tdate", "10 seconds")
                .filter(col("numVotes").geq(500))
                .groupBy(window(col("tdate"), "1 day"), col("tconst"))
                .agg(last("tdate").alias("tdate"),
                        last("numVotes").alias("numVotes"));
    }

    private Dataset<?> latestAgg() {
        return latest()
                .withWatermark("tdate", "10 seconds")
                .groupBy(window(col("tdate"), "1 day"))
                .agg(avg("numVotes").alias("avg_numVotes"))
                .withColumn("window_start", col("window").getField("start"))
                .drop("window")
                .withWatermark("window_start", "10 seconds");
    }

    @PostConstruct
    public void start() throws ToyotaChallengeException {

        var result = titleRatings
                .filter(col("numVotes").geq(500))
                .withWatermark("tdate", "10 seconds")
                .withColumn("tdate_partition", date_trunc("day", col("tdate")))
                .join(latestAgg(), col("tdate_partition").equalTo(col("window_start")), "left")
                .withColumn("rank", RANK);

        try {
            query = result.writeStream()
                    .outputMode("append")
                    .format("csv")
                    .option("path", "/workspaces/toyota-challenge/data/output")
                    .option("checkpointLocation", "/workspaces/toyota-challenge/data/checkpoint")
                    .start();

            query.awaitTermination();
        } catch (StreamingQueryException | TimeoutException e) {
            throw new ToyotaChallengeException(e);
        }
    }

    @PreDestroy
    public void stop() throws ToyotaChallengeException {
        try {
            query.stop();
        } catch (TimeoutException e) {
            throw new ToyotaChallengeException(e);
        }
    }
}
