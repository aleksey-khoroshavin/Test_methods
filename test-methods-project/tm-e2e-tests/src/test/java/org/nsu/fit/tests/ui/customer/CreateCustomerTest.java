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
import org.nsu.fit.tests.ui.screen.AdminScreen;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CreateCustomerTest {
    private Browser browser = null;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private Faker faker;
    private CustomerPojo customerPojo;
    private String createdLogin;
    private AdminScreen result;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        faker = new Faker();
    }

    @Test(description = "Create customer via UI.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer feature")
    public void createCustomer() {
        String customerFirstName = faker.name().firstName();
        String customerLastName = faker.name().lastName();
        String customerLogin = faker.internet().emailAddress();
        String customerPassword = faker.internet().password(7, 11);

        createdLogin = customerLogin;

        result = new LoginScreen(browser)
                .loginAsAdmin()
                .createCustomer()
                .fillEmail(customerLogin)
                .fillPassword(customerPassword)
                .fillFirstName(customerFirstName)
                .fillLastName(customerLastName)
                .clickSubmit();

        List<CustomerPojo> customers = restClient.getCustomers(adminToken, customerLogin);

        assertNotNull(customers);
        assertNotEquals(customers.size(), 0);

        for (CustomerPojo customer : customers) {
            if (customer.firstName.equals(customerFirstName) &&
                    customer.lastName.equals(customerLastName) &&
                    customer.login.equals(customerLogin) &&
                    customer.pass.equals(customerPassword)) {
                customerPojo = customer;
            }
        }

        assertNotNull(customerPojo);
    }

    @Test(description = "Check customer appears in table", dependsOnMethods = "createCustomer")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer feature")
    public void checkCustomerExists() {
        result.searchCustomer(createdLogin);
        assertTrue(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[2]/div/div/div/table/tbody/tr[td='" + createdLogin + "']/td")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
            restClient.deleteCustomer(customerPojo, adminToken);
        }
    }
}
