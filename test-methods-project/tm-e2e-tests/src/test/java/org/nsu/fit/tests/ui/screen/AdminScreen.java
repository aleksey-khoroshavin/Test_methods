package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;
import org.openqa.selenium.By;

public class AdminScreen extends Screen {
    public AdminScreen(Browser browser) {
        super(browser);
        if (!browser.waitPage()) {
            throw new IllegalStateException("Redirect to admin screen failed");
        }
        if (!browser.containsTitle("admin")) {
            throw new IllegalStateException("Can't create Admin Screen");
        }
    }

    public CreateCustomerScreen createCustomer() {
        browser.waitForElement(By.xpath("//button[@title = 'Add Customer']"));
        browser.click(By.xpath("//button[@title = 'Add Customer']"));
        return new CreateCustomerScreen(browser);
    }

    public AdminScreen deleteFirstCreatedCustomer() {
        browser.makeScreenshot();
        String rowXPath = "//*[@id='root']/div/div/div/div/div[1]/div[2]/div/div/div/table/tbody/tr[1]/td[1]/div/";

        browser.waitForElement(By.xpath(rowXPath + "button"));
        browser.click(By.xpath(rowXPath + "button"));

        browser.waitForElement(By.xpath(rowXPath + "button[1]"));
        browser.click(By.xpath(rowXPath + "button[1]"));

        browser.waitElementToBecomeInvisible(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[2]/div/div/div/table/tbody/tr[1]/td[2]/h6"));
        browser.makeScreenshot();

        return this;
    }

    public AdminScreen searchCustomer(String param) {
        browser.waitForElement(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[1]/div[3]/div/input"));
        browser.typeText(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[1]/div[3]/div/input"), param);

        return new AdminScreen(browser);
    }

    public CreatePlanScreen createPlan() {
        browser.click(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[1]/div[4]/div/div/span/button"));
        return new CreatePlanScreen(browser);
    }

    public AdminScreen searchPlan(String param) {
        browser.waitForElement(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[1]/div[3]/div/input"));
        browser.typeText(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[1]/div[3]/div/input"), param);

        return new AdminScreen(browser);
    }

    public AdminScreen deleteFirstCreatedPlan() {
        String rowXPath = "//*[@id='root']/div/div/div/div/div[2]/div[2]/div/div/div/table/tbody/tr[1]/td[1]/div/";

        browser.waitForElement(By.xpath(rowXPath + "button"));
        browser.click(By.xpath(rowXPath + "button"));

        browser.waitForElement(By.xpath(rowXPath + "button[1]"));
        browser.click(By.xpath(rowXPath + "button[1]"));

        browser.waitElementToBecomeInvisible(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[2]/div/div/div/table/tbody/tr[1]/td[2]/h6"));
        browser.makeScreenshot();

        return this;
    }
}
