package org.nsu.fit.tests.ui.screen;

import org.nsu.fit.services.browser.Browser;
import org.nsu.fit.shared.Screen;

public class CreateCustomerScreen extends Screen {
    public CreateCustomerScreen(Browser browser) {
        super(browser);
    }

    public CreateCustomerScreen fillEmail(String email) {
        // TODO: Please implement this...
        return this;
    }

    public CreateCustomerScreen fillPassword(String password) {
        // TODO: Please implement this...
        return this;
    }

    public CreateCustomerScreen fillFirstName(String firstName) {
        // TODO: Please implement this...
        return this;
    }

    public CreateCustomerScreen fillLastName(String lastName) {
        // TODO: Please implement this...
        return this;
    }

    // Лабораторная 4: Подумайте как обработать ситуацию,
    // когда при нажатии на кнопку Submit ('Create') не произойдет переход на AdminScreen,
    // а будет показана та или иная ошибка на текущем скрине.
    public AdminScreen clickSubmit() {
        // TODO: Please implement this...
        return new AdminScreen(browser);
    }

    public AdminScreen clickCancel() {
        // TODO: Please implement this...
        return new AdminScreen(browser);
    }
}
