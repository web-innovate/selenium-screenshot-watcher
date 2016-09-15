package com.github.bogdanlivadariu.screenshotwatcher.util;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonUtil {
    private static JsonDeserializer<ObjectId> objectIdDeserializer = new JsonDeserializer<ObjectId>() {
        @Override
        public ObjectId deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
            return new ObjectId(je.getAsJsonObject().get("$oid").getAsString());
        }
    };

    private static JsonSerializer<ObjectId> objectIDSerializer = new JsonSerializer<ObjectId>() {
        @Override
        public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jo = new JsonObject();
            jo.addProperty("$oid", src.toString());
            return jo;
        }
    };

    public static Gson gson = new GsonBuilder()
        .registerTypeAdapter(ObjectId.class, objectIdDeserializer)
        .registerTypeAdapter(ObjectId.class, objectIDSerializer)
        .create();

}
