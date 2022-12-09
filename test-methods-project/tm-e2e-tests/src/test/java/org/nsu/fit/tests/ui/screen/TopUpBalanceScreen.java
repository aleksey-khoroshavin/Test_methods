package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;
import org.openqa.selenium.By;

public class TopUpBalanceScreen extends Screen {
    public TopUpBalanceScreen(Browser browser) {
        super(browser);
    }

    public TopUpBalanceScreen fillMoney(Integer money) {
        browser.makeScreenshot();
        browser.typeText(By.name("money"), String.valueOf(money));
        browser.makeScreenshot();
        return this;
    }

    public CustomerScreen clickSubmit() throws IllegalArgumentException {
        browser.makeScreenshot();
        browser.click(By.xpath("//button[@type = 'submit']"));
        browser.makeScreenshot();
        return new CustomerScreen(browser);
    }

    public CustomerScreen clickCancel() {
        browser.click(By.xpath("//button"));
        return new CustomerScreen(browser);
    }
}
