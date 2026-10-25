package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Prueba de integración: requiere base de datos MySQL activa")
@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
