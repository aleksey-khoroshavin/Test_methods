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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class CancelPlanCreateTest {
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

    @Test(description = "Cancel creation plan via UI.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Cancel creating plan feature")
    public void createPlan() {
        String details = faker.commerce().productName();
        planName = faker.name().title();
        int fee = faker.number().numberBetween(1, 10);

        result = new LoginScreen(browser)
                .loginAsAdmin()
                .createPlan()
                .fillFee(fee)
                .fillName(planName)
                .fillDetails(details)
                .clickCancel();

        List<PlanPojo> plans = restClient.getPlans(adminToken);
        assertNotNull(plans);

        for (PlanPojo planPojo : plans) {
            if (planPojo.details.equals(details) &&
                    planPojo.fee == fee &&
                    planPojo.name.equals(planName)) {
                plan = planPojo;
            }
        }
        assertNull(plan);
    }

    @Test(description = "Check plan not appears in table", dependsOnMethods = "createPlan")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Cancel creating plan feature")
    public void checkPlanExists() {
        result.searchPlan(planName);
        assertFalse(browser.isElementPresent(By.xpath("//*[@id='root']/div/div/div/div/div[2]/div[2]/div/div/div/table/tbody/tr[td='" + planName + "']/td")));
    }

    @AfterClass
    public void afterClass() {
        if (browser != null) {
            browser.close();
        }
    }
}
