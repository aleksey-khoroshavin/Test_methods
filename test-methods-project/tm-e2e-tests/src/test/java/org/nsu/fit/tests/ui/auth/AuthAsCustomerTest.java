package org.nsu.fit.tests.ui.auth;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.services.browser.BrowserService;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class AuthAsCustomerTest {
    private Browser browser = null;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private CustomerPojo customer;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        Faker faker = new Faker();

        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.login = faker.internet().emailAddress();
        customerPojo.pass = faker.internet().password(7, 11);
        customer = restClient.createCustomer(customerPojo, adminToken);
    }

    @Test(description = "Authenticate as customer via UI")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as customer feature")
    public void loginAsCustomer() {
        new LoginScreen(browser).loginAsCustomer(customer.login, customer.pass);
    }

    @Test(description = "Check for no admin rights", dependsOnMethods = "loginAsCustomer")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication as customer feature")
    public void checkNoRightToCreateCustomers() {
        assertFalse(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[1]/div[4]/div/div/span/button")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
            restClient.deleteCustomer(customer, adminToken);
        }
    }

}
