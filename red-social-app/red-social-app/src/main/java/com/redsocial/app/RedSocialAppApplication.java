package com.redsocial.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SpringBootApplication
public class RedSocialAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedSocialAppApplication.class, args);
	}

	@Bean
	CommandLineRunner dbConnectionTest(DataSource dataSource) {
		return args -> {
			Logger log = LoggerFactory.getLogger(RedSocialAppApplication.class);
			try (Connection conn = dataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement("SELECT 1");
				 ResultSet rs = ps.executeQuery()) {

				DatabaseMetaData md = conn.getMetaData();
				String url = md.getURL();
				String user = md.getUserName();
				String driver = md.getDriverName() + " " + md.getDriverVersion();

				if (rs.next()) {
					log.info("Prueba de conexión a BD OK. URL={}, usuario={}, driver={}", url, user, driver);
				} else {
					log.warn("Conexión establecida pero la prueba 'SELECT 1' no devolvió filas. URL={}, usuario={}", url, user);
				}
			} catch (Exception e) {
				log.error("Fallo en la prueba de conexión a la base de datos.", e);
				throw e; // Opcional: volver a lanzar para fallar el arranque
			}
		};
	}
}
