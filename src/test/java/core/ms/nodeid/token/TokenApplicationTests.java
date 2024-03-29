package core.ms.nodeid.token;

import core.ms.nodeid.token.model.Token;
import core.ms.nodeid.token.repository.TokenRepository;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenApplicationTests {

	@LocalServerPort
	private Integer port;

	@Autowired
	private TokenRepository tokenRepository;

	@Container
	static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:15"));

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.username", container::getUsername);
		registry.add("spring.datasource.password", container::getPassword);

		log.info("url {}", container.getJdbcUrl());
		log.info("username {}", container.getUsername());
		log.info("password {}", container.getPassword());
		log.info("spring.datasource.driver-class-name {}", container.getJdbcDriverInstance());
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + port;
		tokenRepository.deleteAll();
	}

	@BeforeAll
	static void beforeAll() {
		container.start();
	}

	@AfterAll
	static void afterAll() {
		container.stop();
	}

	@Test
	void test() {
			Long id = 1L;
			String token = "meuToken";
			LocalDateTime timestamp = LocalDateTime.now();

			Token object = new Token(id, token, timestamp);

			assertNotNull(object);
			assertEquals(id, object.getId());
			assertEquals(token, object.getToken());
			assertEquals(timestamp, object.getTimestamp());
	}
}