package org.nsu.fit.tests.api.error;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class CreateExistingLoginCustomerTest {
    private static final String TEST_LOGIN = "existing@login.com";
    private RestClient restClient;
    private Faker faker;
    private AccountTokenPojo adminToken;
    private CustomerPojo existingCustomer;

    @BeforeClass
    public void beforeClass() {
        restClient = new RestClient();
        faker = new Faker();
    }

    @Test(description = "Authenticate as admin")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with already existing login feature")
    public void authAsAdminTest() {
        adminToken = restClient.authenticate("admin", "setup");
        assertNotNull(adminToken);
    }

    @Test(description = "Create customer", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with already existing login feature")
    public void createCustomerTest() {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.pass = faker.internet().password(7, 11);
        customerPojo.login = TEST_LOGIN;
        customerPojo.balance = faker.number().numberBetween(0, 100);

        existingCustomer = restClient.createCustomer(customerPojo, adminToken);
        assertNotNull(existingCustomer);
        assertEquals(customerPojo.firstName, existingCustomer.firstName);
        assertEquals(customerPojo.lastName, existingCustomer.lastName);
        assertEquals(customerPojo.login, existingCustomer.login);
        assertEquals(customerPojo.balance, existingCustomer.balance);
    }

    @Test(description = "Create another customer", dependsOnMethods = "createCustomerTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer with already existing login feature")
    public void createAnotherCustomerTest() {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();
        customerPojo.pass = faker.internet().password(7, 11);
        customerPojo.login = TEST_LOGIN;
        customerPojo.balance = faker.number().numberBetween(0, 100);

        CustomerPojo result = restClient.createCustomer(customerPojo, adminToken);
        assertNull(result);
    }

    @AfterClass
    public void afterClass() {
        restClient.deleteCustomer(existingCustomer, adminToken);
    }
}
