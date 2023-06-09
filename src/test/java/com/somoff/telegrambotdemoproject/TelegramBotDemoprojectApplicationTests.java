package com.somoff.telegrambotdemoproject;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class TelegramBotDemoprojectApplicationTests {
	@Value("${bot.username}")
	private String botUsername;
	@Value("${bot.token}")
	private String botToken;


	@BeforeEach
	void setUp() {
		System.out.println("Запущен тест");
		RestAssured.baseURI = "https://api.telegram.org/bot"+botToken;

	}


	@Test
	void successSendMessage() {
		given()
				.param("text", "rest-assured_TEST_SUCCESS")
				.param("chat_id", "178458443")
				.when()
				.get("/sendMessage")
				.then()
				.statusCode(200);
	}


	@Test
	void getBotUsername() {
		given()
				.when()
				.get("/getMe")
				.then()
				.statusCode(200)
				.assertThat()
				.body("result.username", equalTo(botUsername));
	}

}
