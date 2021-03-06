package com.microsoft.services.odata.impl;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.services.odata.interfaces.JsonSerializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.microsoft.services.odata.Helpers.getODataTypePrefix;
import static com.microsoft.services.odata.Helpers.getReservedNames;
import static com.microsoft.services.odata.Helpers.getReservedODataTypePrefix;
import static com.microsoft.services.odata.Helpers.getReservedPrefix;

/**
 * The type Gson serializer.
 */
public abstract class GsonSerializerBase implements JsonSerializer {


    private Gson createGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .registerTypeAdapter(Calendar.class, new CalendarTypeAdapter())
                .registerTypeAdapter(GregorianCalendar.class, new CalendarTypeAdapter())
                .registerTypeAdapter(byte[].class, getByteArrayTypeAdapter())
                .create();
    }

    protected abstract ByteArrayTypeAdapterBase getByteArrayTypeAdapter();

    @Override
    public String serialize(Object objectToSerialize) {
        Gson serializer = createGson();
        JsonElement json = serializer.toJsonTree(objectToSerialize);
        sanitizePostSerialization(json);

        return json.toString();
    }

    @Override
    public <E> E deserialize(String payload, Class<E> clazz) {
        Gson serializer = createGson();
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(payload);
        sanitizeForDeserialization(json);


        return serializer.fromJson(json, clazz);
    }

    @Override
    public <E> List<E> deserializeList(String payload, Class<E> clazz) {
        Class arrayClass = Array.newInstance(clazz, 0).getClass();

        Gson serializer = createGson();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(payload);

        JsonElement jsonArray = json.get("value");
        sanitizeForDeserialization(jsonArray);

        E[] array = (E[]) serializer.fromJson(jsonArray, arrayClass);

        ArrayList<E> arrayList = new ArrayList<E>();

        for (int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }

        return arrayList;
    }

    private void sanitizePostSerialization(JsonElement json) {
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement subElement : jsonArray) {
                sanitizePostSerialization(subElement);
            }
        } else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();


            Set<Map.Entry<String, JsonElement>> entries = new HashSet<Map.Entry<String, JsonElement>>(jsonObject.entrySet());

            for (Map.Entry<String, JsonElement> entry : entries) {
                String propertyName = entry.getKey();
                JsonElement subElement = entry.getValue();

                String newName = propertyName;

                if (newName.startsWith(getReservedPrefix())) {
                    newName = newName.substring(getReservedPrefix().length());
                    if (getReservedNames().contains(newName)) {
                        jsonObject.remove(newName);
                        jsonObject.add(propertyName, subElement);
                    }
                } else if (propertyName.equals(getReservedODataTypePrefix())) {
                    jsonObject.remove(getReservedODataTypePrefix());
                    jsonObject.add(getODataTypePrefix(), subElement);
                }

                sanitizePostSerialization(subElement);
            }
        }

    }

    private void sanitizeForDeserialization(JsonElement json) {
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement subElement : jsonArray) {
                sanitizeForDeserialization(subElement);
            }
        } else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            Set<Map.Entry<String, JsonElement>> entries = new HashSet<Map.Entry<String, JsonElement>>(jsonObject.entrySet());

            for (Map.Entry<String, JsonElement> entry : entries) {
                String propertyName = entry.getKey();
                JsonElement subElement = entry.getValue();

                String newName = getReservedPrefix() + propertyName;
                if (getReservedNames().contains(propertyName)) {
                    jsonObject.remove(propertyName);
                    jsonObject.add(newName, subElement);
                } else {
                    String oDataTypeName = getReservedODataTypePrefix();
                    if (propertyName.equals(getODataTypePrefix())) {
                        jsonObject.remove(propertyName);
                        jsonObject.add(oDataTypeName, subElement);
                    }
                }

                sanitizePostSerialization(subElement);
            }
        }
    }
}
