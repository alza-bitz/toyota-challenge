package com.alzatech.toyota_challenge.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TitleRatingRecord {

    // @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    // @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    // @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime tdate;

    private String tconst;

    private Double averageRating;

    private Long numVotes;

    // @lombok.
    // private List<String> errors;

    public TitleRatingRecord(String... data) {
        if (data.length > 0) {
            tdate = null;
        } else {
            tdate = null;
        }
        if (data.length > 1) {
            tconst = data[1];
        } else {
            tconst = "";
        }
        if (data.length > 2) {
            averageRating = Double.parseDouble(data[2]);
        } else {
            averageRating = 0.0;
        }
        if (data.length > 3) {
            numVotes = Long.parseLong(data[3]);
        } else {
            numVotes = 0L;
        }
    }
}
