package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;
import org.openqa.selenium.By;

public class CustomerScreen extends Screen {
    public CustomerScreen(Browser browser) {
        super(browser);
    }

    public TopUpBalanceScreen topUpBalance() {
        browser.waitForElement(By.xpath("//*[@id='root']/div/div/div/div/p[1]/a"));
        browser.click(By.xpath("//*[@id='root']/div/div/div/div/p[1]/a"));
        return new TopUpBalanceScreen(browser);
    }

    public CustomerScreen searchPlan(String param) {
        browser.waitForElement(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[1]/div[3]/div/input"));
        browser.typeText(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[1]/div[3]/div/input"), param);

        return new CustomerScreen(browser);
    }

    public CustomerScreen subscribeToFirstPlanInTable() {
        browser.makeScreenshot();
        String rowXPath = "//*[@id='root']/div/div/div/div/div[2]/div[2]/div/div/div/table/tbody/tr[1]/td[1]/div/";
        browser.waitForElement(By.xpath(rowXPath + "button"));
        browser.click(By.xpath(rowXPath + "button"));
        browser.waitForElement(By.xpath(rowXPath + "button[1]"));
        browser.click(By.xpath(rowXPath + "button[1]"));
        browser.makeScreenshot();
        return this;
    }
}
