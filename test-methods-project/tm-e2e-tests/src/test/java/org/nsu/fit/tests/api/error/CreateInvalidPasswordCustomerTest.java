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

public class CreateInvalidPasswordCustomerTest {
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
    @Feature("Create customer with invalid password feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create with invalid password", dependsOnMethods = "authAsAdminTest")
    @Parameters("password")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with invalid password feature")
    public void nullPasswordTest(@Optional String password) {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.pass = password;
        customerPojo.login = faker.internet().emailAddress();
        customerPojo.balance = faker.number().numberBetween(0, 100);

        CustomerPojo result = restClient.createCustomer(customerPojo, adminToken);
        assertNull(result);
    }
}
