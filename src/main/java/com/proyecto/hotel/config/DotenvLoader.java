package com.proyecto.hotel.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .filename(".env")
                .ignoreIfMissing()
                .load();
        if (dotenv.entries().isEmpty()) {
            dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir") + "/BackendHotel")
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
        }
        // Pasar las variables del .env a System.properties para que Spring las encuentre
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}