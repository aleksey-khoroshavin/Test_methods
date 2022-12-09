package org.nsu.fit.tests.ui.plan;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.services.browser.BrowserService;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.nsu.fit.services.rest.data.PlanPojo;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class SubscribeToPlanTest {
    private Browser browser = null;
    private String login;
    private String password;
    private String planDetails;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private CustomerPojo customer;
    private PlanPojo plan;
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
    @Feature("Subscribe to plan feature")
    public void createTestCustomer() {
        login = faker.internet().emailAddress();
        password = faker.internet().password(7, 11);

        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.login = login;
        customerPojo.pass = password;
        customerPojo.firstName = faker.name().firstName();
        customerPojo.lastName = faker.name().lastName();

        customer = restClient.createCustomer(customerPojo, adminToken);
        AccountTokenPojo accountToken = restClient.authenticate(login, password);

        assertNotNull(customer);
        assertNotNull(accountToken);
    }

    @Test(description = "Create plan as admin.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Subscribe to plan feature")
    public void createPlan() {
        PlanPojo planPojo = new PlanPojo();
        planPojo.details = planDetails = faker.commerce().productName();
        planPojo.name = faker.name().title();
        planPojo.fee = faker.number().numberBetween(1, 10);

        plan = restClient.createPlan(planPojo, adminToken);
        assertNotNull(plan);
        assertEquals(planPojo.name, plan.name);
    }

    @Test(description = "Subscribe to the first Plan via UI.", dependsOnMethods = {"createTestCustomer", "createPlan"})
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Subscribe to plan feature")
    public void subscribeToFirstPlanTest() {
        new LoginScreen(browser)
                .loginAsCustomer(login, password)
                .searchPlan(planDetails)
                .subscribeToFirstPlanInTable();

        List<PlanPojo> subscriptions = restClient.getAvailablePlans(login);

        assertNotNull(subscriptions);
        assertNotEquals(subscriptions.size(), 0);
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
            restClient.deletePlanPojo(plan, adminToken);
            restClient.deleteCustomer(customer, adminToken);
        }
    }
}
