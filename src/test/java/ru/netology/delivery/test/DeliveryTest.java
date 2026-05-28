package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        Selenide.open("http://localhost:9999/");
        $("[data-test-id='city'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .sendKeys(DataGenerator.generateCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .sendKeys(firstMeetingDate);
        $("[data-test-id='name'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .sendKeys(validUser.getName());
        $("[data-test-id='phone'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Запланировать")).click();

        $("[data-test-id='success-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Успешно!"), Condition.text("Встреча успешно запланирована на " + firstMeetingDate));

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .sendKeys(secondMeetingDate);
        $$("button").find(Condition.text("Запланировать")).click();

        $("[data-test-id='replan-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Необходимо подтверждение"), Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        $$("button").find(Condition.text("Перепланировать")).click();

        $("[data-test-id='success-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Успешно!"), Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
