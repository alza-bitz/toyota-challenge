package com.alzatech.toyota_challenge.dao;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Repository;

import static org.apache.spark.sql.functions.*;

import com.alzatech.toyota_challenge.model.TitleRatingRecord;

@Repository
public class TitleRatingFileDataReader implements DataReader<TitleRatingRecord> {

    private final SparkSession sparkSession;

    private final StructType logSchema = new StructType()
            .add("tconst", DataTypes.StringType)
            .add("averageRating", DataTypes.DoubleType)
            .add("numVotes", DataTypes.LongType)
            .add("tdate", DataTypes.TimestampType);

    public TitleRatingFileDataReader(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public Dataset<TitleRatingRecord> readData() {
        return sparkSession
                .readStream()
                .option("sep", "\t")
                .schema(logSchema)
                .csv("/workspaces/toyota-challenge/data/input")
                .withColumn("tdate", lit("2025-02-24").cast("timestamp")) // TODO temp hack
                .as(Encoders.bean(TitleRatingRecord.class));
    }

    public Dataset<TitleRatingRecord> readDataFromSocket() {
        return sparkSession
                .readStream()
                .format("socket")
                .option("host", "localhost")
                .option("port", 9999)
                .load().as(Encoders.STRING())
                .map((MapFunction<String, TitleRatingRecord>) data -> 
                        new TitleRatingRecord(data.split("\t")), Encoders.bean(TitleRatingRecord.class))
                .as(Encoders.bean(TitleRatingRecord.class));
    }

}
