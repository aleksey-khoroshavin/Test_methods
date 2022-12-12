package org.nsu.fit.tests.ui.customer;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TopUpCustomerBalanceTest {
    private Browser browser = null;
    private String login;
    private String password;
    private AccountTokenPojo adminToken;
    private AccountTokenPojo accountToken;
    private CustomerPojo customer;
    private RestClient restClient;
    private Faker faker;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        faker = new Faker();
    }

    @Test(description = "Create user for test as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Top up customer balance feature")
    public void createTestCustomer() {
        login = faker.internet().emailAddress();
        password = faker.internet().password(7, 11);

        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.login = login;
        customerPojo.pass = password;
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();

        customer = restClient.createCustomer(customerPojo, adminToken);
        accountToken = restClient.authenticate(login, password);

        assertNotNull(customer);
        assertNotNull(accountToken);
    }

    @Test(description = "Login as Customer via UI.", dependsOnMethods = "createTestCustomer")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Top up customer balance feature")
    public void topUpCustomerBalance() {
        int topUpSum = faker.number().numberBetween(1, 10);
        CustomerPojo customerPojo = restClient.meAsCustomer(accountToken);
        new LoginScreen(browser)
                .loginAsCustomer(login, password)
                .topUpBalance()
                .fillMoney(topUpSum)
                .clickSubmit();

        assertEquals(customerPojo.balance + topUpSum,
                restClient.meAsCustomer(accountToken).balance);
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
            restClient.deleteCustomer(customer, adminToken);
        }
    }
}
