package com.grdvp.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class JsonbListConverter implements AttributeConverter<List<String>, String> {

    private static final Gson GSON = new Gson();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return GSON.toJson(attribute != null ? attribute : new ArrayList<>());
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        return GSON.fromJson(dbData, new TypeToken<List<String>>() {}.getType());
    }
}