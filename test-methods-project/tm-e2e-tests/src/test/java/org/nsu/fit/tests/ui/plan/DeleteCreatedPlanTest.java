package org.nsu.fit.tests.ui.plan;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.services.browser.BrowserService;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.PlanPojo;
import org.nsu.fit.tests.ui.screen.AdminScreen;
import org.nsu.fit.tests.ui.screen.LoginScreen;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class DeleteCreatedPlanTest {
    private Browser browser = null;
    private RestClient restClient;
    private AccountTokenPojo adminToken;
    private Faker faker;
    private String planName;
    private PlanPojo plan;
    private AdminScreen result;

    @BeforeClass
    public void beforeClass() {
        browser = BrowserService.openNewBrowser();
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        faker = new Faker();
    }

    @Test(description = "Create plan via UI.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void createPlan() {
        String planDetails = faker.commerce().productName();
        planName = faker.name().title();
        int planFee = faker.number().numberBetween(1, 10);

        result = new LoginScreen(browser)
                .loginAsAdmin()
                .createPlan()
                .fillFee(planFee)
                .fillName(planName)
                .fillDetails(planDetails)
                .clickSubmit();

        List<PlanPojo> plans = restClient.getPlans(adminToken);

        assertNotNull(plans);
        assertNotEquals(plans.size(), 0);

        for (PlanPojo planPojo : plans) {
            if (planPojo.name.equals(planName) &&
                    planPojo.fee == planFee &&
                    planPojo.details.equals(planDetails)) {
                plan = planPojo;
            }
        }
        assertNotNull(plan);
    }

    @Test(description = "Delete plan via UI.",
            dependsOnMethods = {"createPlan"})
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete plan feature")
    public void deletePlanTest() {
        result.searchPlan(planName).deleteFirstCreatedPlan();
        assertFalse(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[1]/div[2]/div/div/div/table/tbody/tr[td='" + planName + "']/td")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
        }
    }
}
