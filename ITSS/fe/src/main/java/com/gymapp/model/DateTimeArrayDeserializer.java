package com.gymapp.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class DateTimeArrayDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.START_ARRAY) {
            // Parse array format [year, month, day, hour, minute, second, nanosecond]
            p.nextToken(); // year
            int year = p.getIntValue();
            p.nextToken(); // month
            int month = p.getIntValue();
            p.nextToken(); // day
            int day = p.getIntValue();
            p.nextToken(); // hour
            int hour = p.getIntValue();
            p.nextToken(); // minute
            int minute = p.getIntValue();
            p.nextToken(); // second
            int second = p.getIntValue();
            p.nextToken(); // nanosecond
            int nanosecond = p.getIntValue();
            p.nextToken(); // move past END_ARRAY

            return LocalDateTime.of(year, month, day, hour, minute, second, nanosecond);
        } else if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            return LocalDateTime.parse(p.getText());
        } else if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else {
            return LocalDateTime.now();
        }
    }
}