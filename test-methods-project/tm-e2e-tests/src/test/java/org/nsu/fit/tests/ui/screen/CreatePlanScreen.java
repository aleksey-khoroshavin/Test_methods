package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;
import org.openqa.selenium.By;

public class CreatePlanScreen extends Screen {
    public CreatePlanScreen(Browser browser) {
        super(browser);
    }

    public CreatePlanScreen fillName(String name) {
        browser.typeText(By.name("name"), name);
        return this;
    }

    public CreatePlanScreen fillDetails(String details) {
        browser.typeText(By.name("details"), details);
        return this;
    }

    public CreatePlanScreen fillFee(Integer fee) {
        browser.typeText(By.name("fee"), String.valueOf(fee));
        return this;
    }

    public AdminScreen clickSubmit() throws IllegalArgumentException {
        browser.click(By.xpath("//button[@type = 'submit']"));
        return new AdminScreen(browser);
    }

    public AdminScreen clickCancel() {
        browser.click(By.xpath("//button"));
        return new AdminScreen(browser);
    }
}
