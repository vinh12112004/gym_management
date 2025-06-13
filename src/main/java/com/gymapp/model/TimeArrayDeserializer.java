package com.gymapp.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TimeArrayDeserializer extends JsonDeserializer<String> {
    
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.START_ARRAY) {
            // Parse LocalTime array format [hour, minute] from backend
            p.nextToken(); // move to first element (hour)
            int hour = p.getIntValue();
            p.nextToken(); // move to second element (minute)
            int minute = p.getIntValue();
            p.nextToken(); // move past END_ARRAY
            
            // Format as HH:mm string for frontend display
            return String.format("%02d:%02d", hour, minute);
        } else if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            // If backend sends string format, return as-is
            return p.getText();
        } else if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else {
            // Handle unexpected format
            return "00:00";
        }
    }
}