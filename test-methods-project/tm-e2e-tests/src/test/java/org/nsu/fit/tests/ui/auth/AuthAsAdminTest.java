package org.nsu.fit.tests.ui.auth;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.services.browser.BrowserService;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class AuthAsAdminTest {
    private Browser browser = null;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
    }

    @Test(description = "Authenticate as admin via UI")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as admin feature")
    public void loginAsAdmin() {
        new LoginScreen(browser).loginAsAdmin();
    }

    @Test(description = "Check for available admin right", dependsOnMethods = "loginAsAdmin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as admin feature")
    public void checkRightToCreateCustomers() {
        assertTrue(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[1]/div[4]/div/div/span/button")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
        }
    }
}
