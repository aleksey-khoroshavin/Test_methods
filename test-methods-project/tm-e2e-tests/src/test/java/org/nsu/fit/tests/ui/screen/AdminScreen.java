package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;

public class AdminScreen extends Screen {
    public AdminScreen(Browser browser) {
        super(browser);
    }

    public CreateCustomerScreen createCustomer() {
        // TODO: Please implement this...
        return new CreateCustomerScreen(browser);
    }
}
