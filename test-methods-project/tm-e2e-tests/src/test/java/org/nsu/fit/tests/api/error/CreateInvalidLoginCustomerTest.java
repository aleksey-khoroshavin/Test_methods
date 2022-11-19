package org.nsu.fit.tests.api.error;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class CreateInvalidLoginCustomerTest {
    private RestClient restClient;
    private Faker faker;
    private AccountTokenPojo adminToken;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
        faker = new Faker();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with invalid login feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create with invalid login", dependsOnMethods = "authAsAdminTest")
    @Parameters("login")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with invalid login feature")
    public void nullPasswordTest(@Optional String login) {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.pass = faker.internet().password(7, 11);
        customerPojo.login = login;
        customerPojo.balance = faker.number().numberBetween(0, 100);

        CustomerPojo result = restClient.createCustomer(customerPojo, adminToken);
        assertNull(result);
    }
}
