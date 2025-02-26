package com.alzatech.toyota_challenge.dao;

import org.apache.spark.sql.Dataset;

public interface DataReader<R> {
    Dataset<R> readData();
}
