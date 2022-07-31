package com.kafka.config;

import com.google.gson.*;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyJsonSerializer implements Serializer {
    private static class LocalDateAdapter implements JsonSerializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .create();

    @Override
    public byte[] serialize(String s, Object o) {
        return gson.toJson(o).getBytes(StandardCharsets.UTF_8);
    }
}
