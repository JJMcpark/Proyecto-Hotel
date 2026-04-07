package com.proyecto.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.proyecto.hotel.config.DotenvLoader;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class HotelApplication {

	@PostConstruct
	void timezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
	}

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(HotelApplication.class, args);
	}

}
