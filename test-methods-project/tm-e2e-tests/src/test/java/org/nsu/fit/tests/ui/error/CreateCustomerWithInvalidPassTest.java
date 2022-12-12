package org.nsu.fit.tests.ui.error;

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

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertThrows;

public class CreateCustomerWithInvalidPassTest {
    private Browser browser = null;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private Faker faker;
    private CustomerPojo customerPojo;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        faker = new Faker();
    }

    @Test(description = "Create customer via UI with invalid password")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create invalid customer feature")
    public void createCustomer() {
        String customerFirstName = faker.name().firstName();
        String customerLastName = faker.name().lastName();
        String customerLogin = faker.internet().emailAddress();
        String customerPassword = faker.internet().password(1, 6);

        assertThrows(IllegalStateException.class, () -> new LoginScreen(browser)
                .loginAsAdmin()
                .createCustomer()
                .fillEmail(customerLogin)
                .fillPassword(customerPassword)
                .fillFirstName(customerFirstName)
                .fillLastName(customerLastName)
                .clickSubmit());

        List<CustomerPojo> customers = restClient.getCustomers(adminToken, customerLogin);

        assertNotNull(customers);

        for (CustomerPojo customer : customers) {
            if (customer.firstName.equals(customerFirstName) &&
                    customer.lastName.equals(customerLastName) &&
                    customer.login.equals(customerLogin) &&
                    customer.pass.equals(customerPassword)) {
                customerPojo = customer;
            }
        }
        assertNull(customerPojo);
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
        }
    }
}
