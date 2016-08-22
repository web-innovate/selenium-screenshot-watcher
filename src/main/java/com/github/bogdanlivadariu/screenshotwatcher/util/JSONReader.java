package com.github.bogdanlivadariu.screenshotwatcher.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONReader<T> {

    private static Logger logger = LogManager.getLogger(JSONReader.class);

    private String fileName;

    private Class<T> typeOfT;

    public JSONReader(Class<T> typeOfT) {
        this.typeOfT = typeOfT;
    }

    public JSONReader(String filename, Class<T> typeOfT) {
        this.fileName = filename;
        this.typeOfT = typeOfT;
    }

    public T readJSONFile() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(this.fileName);
        try (Reader reader = new InputStreamReader(inputStream)) {
            logger.info("Loaded file: " + this.fileName);
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader, typeOfT);

        }
    }

    public T readJSONFromInputStream(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader, typeOfT);
        }
    }
}
