package com.proyecto.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.proyecto.hotel.config.DotenvLoader;

@SpringBootApplication
public class HotelApplication {

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(HotelApplication.class, args);
	}

}
