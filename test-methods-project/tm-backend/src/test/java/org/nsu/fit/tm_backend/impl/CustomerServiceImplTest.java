package org.nsu.fit.tm_backend.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nsu.fit.tm_backend.repository.Repository;
import org.nsu.fit.tm_backend.repository.data.ContactPojo;
import org.nsu.fit.tm_backend.repository.data.CustomerPojo;
import org.nsu.fit.tm_backend.service.impl.CustomerServiceImpl;
import org.nsu.fit.tm_backend.service.impl.auth.data.AuthenticatedUserDetails;
import org.nsu.fit.tm_backend.shared.Globals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Лабораторная 2: покрыть unit тестами класс CustomerServiceImpl на 100%.
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private Repository repository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testCreateCustomer() {
        // arrange: готовим входные аргументы и настраиваем mock'и.
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = "Baba_Jaga";
        createCustomerInput.balance = 0;

        CustomerPojo createCustomerOutput = new CustomerPojo();
        createCustomerOutput.id = UUID.randomUUID();
        createCustomerOutput.firstName = "John";
        createCustomerOutput.lastName = "Wick";
        createCustomerOutput.login = "john_wick@example.com";
        createCustomerOutput.pass = "Baba_Jaga";
        createCustomerOutput.balance = 0;

        when(repository.createCustomer(createCustomerInput)).thenReturn(createCustomerOutput);

        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customer = customerService.createCustomer(createCustomerInput);

        // assert: проверяем результат выполнения метода.
        assertEquals(customer.id, createCustomerOutput.id);

        // Проверяем, что метод по созданию Customer был вызван ровно 1 раз с определенными аргументами
        verify(repository, times(1)).createCustomer(createCustomerInput);

        // Проверяем, что другие методы не вызывались...
        verify(repository, times(0)).getCustomers();
    }

    @Test
    void testCreateCustomerWithNullArgument_Right() {
        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                customerService.createCustomer(null));
        assertEquals("Argument 'customer' is null.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "123qwe:Password is very easy.",
            "null:Field 'customer.pass' is null.",
            "123qw:Password's length should be more or equal 6 symbols and less or equal 12 symbols.",
            "tooLongPassword:Password's length should be more or equal 6 symbols and less or equal 12 symbols."
    }, delimiter = ':', nullValues = {"null"})
    void testCreateCustomerWithInvalidPassword(String password, String message) {
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = password;
        createCustomerInput.balance = 0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals(message, exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null:Field 'customer.login' is null.",
            "john_wickexample.com:Wrong format of field 'customer.login'."
    }, delimiter = ':', nullValues = {"null"})
    void testCreateCustomerWithInvalidLogin(String login, String message) {
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = login;
        createCustomerInput.pass = "password";
        createCustomerInput.balance = 0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCreateCustomerWithLoginAlreadyExist() {
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "Alice";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "wick@example.com";
        createCustomerInput.pass = "alicepass";
        createCustomerInput.balance = 0;

        CustomerPojo createCustomerOutput = new CustomerPojo();
        createCustomerOutput.id = UUID.randomUUID();
        createCustomerOutput.firstName = "Bob";
        createCustomerOutput.lastName = "Wick";
        createCustomerOutput.login = "wick@example.com";
        createCustomerOutput.pass = "bobpass";
        createCustomerOutput.balance = 0;

        when(repository.getCustomerByLogin(createCustomerInput.login)).thenReturn(createCustomerOutput);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Customer with this login already exists.", exception.getMessage());
        verify(repository, times(1)).getCustomerByLogin(createCustomerInput.login);
        verify(repository, times(0)).createCustomer(createCustomerInput);
    }

    @Test
    void testGetCustomers() {
        CustomerPojo customerAlice = new CustomerPojo();
        customerAlice.id = UUID.randomUUID();
        customerAlice.firstName = "Alice";
        customerAlice.lastName = "Wick";
        customerAlice.login = "alice_wick@example.com";
        customerAlice.pass = "alicepass";
        customerAlice.balance = 0;

        CustomerPojo customerBob = new CustomerPojo();
        customerBob.id = UUID.randomUUID();
        customerBob.firstName = "Bob";
        customerBob.lastName = "Wick";
        customerBob.login = "bob_wick@example.com";
        customerBob.pass = "bobpass";
        customerBob.balance = 0;

        Set<CustomerPojo> customers = new HashSet<>();
        customers.add(customerAlice);
        customers.add(customerBob);

        when(repository.getCustomers()).thenReturn(customers);
        Set<CustomerPojo> resultSet = customerService.getCustomers();

        assertEquals(customers, resultSet);
        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).getCustomerByLogin(customerAlice.login);
        verify(repository, times(0)).createCustomer(customerAlice);
    }

    @Test
    void testGetCustomerIds() {
        UUID customer1 = UUID.randomUUID();
        UUID customer2 = UUID.randomUUID();
        UUID customer3 = UUID.randomUUID();

        Set<UUID> customerIds = new HashSet<>();
        customerIds.add(customer1);
        customerIds.add(customer2);
        customerIds.add(customer3);

        when(repository.getCustomerIds()).thenReturn(customerIds);
        Set<UUID> resultSet = customerService.getCustomerIds();

        assertEquals(customerIds, resultSet);
        verify(repository, times(1)).getCustomerIds();
    }

    @Test
    void testGetCustomerById() {
        UUID uuid = UUID.randomUUID();

        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = uuid;
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "password";
        customerPojo.balance = 0;

        when(repository.getCustomer(uuid)).thenReturn(customerPojo);
        CustomerPojo result = customerService.getCustomer(uuid);

        assertEquals(customerPojo, result);
        verify(repository, times(1)).getCustomer(uuid);
    }

    @Test
    void testLookUpCustomerByExistingUUID() {
        UUID uuid = UUID.randomUUID();

        CustomerPojo customerAlice = new CustomerPojo();
        customerAlice.id = uuid;
        customerAlice.firstName = "Alice";
        customerAlice.lastName = "Wick";
        customerAlice.login = "alice_wick@example.com";
        customerAlice.pass = "alicepass";
        customerAlice.balance = 0;

        CustomerPojo customerBob = new CustomerPojo();
        customerBob.id = UUID.randomUUID();
        customerBob.firstName = "Bob";
        customerBob.lastName = "Wick";
        customerBob.login = "bob_wick@example.com";
        customerBob.pass = "bobpass";
        customerBob.balance = 0;

        Set<CustomerPojo> customers = new HashSet<>();
        customers.add(customerAlice);
        customers.add(customerBob);

        when(repository.getCustomers()).thenReturn(customers);
        CustomerPojo result = customerService.lookupCustomer(uuid);

        assertEquals(customerAlice, result);
        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).getCustomer(customerAlice.id);
        verify(repository, times(0)).getCustomer(customerBob.id);
    }

    @Test
    void testLookUpCustomerByNotExistingUUID() {
        UUID uuid = UUID.randomUUID();

        CustomerPojo customerAlice = new CustomerPojo();
        customerAlice.id = UUID.randomUUID();
        customerAlice.firstName = "Alice";
        customerAlice.lastName = "Wick";
        customerAlice.login = "alice_wick@example.com";
        customerAlice.pass = "alicepass";
        customerAlice.balance = 0;

        CustomerPojo customerBob = new CustomerPojo();
        customerBob.id = UUID.randomUUID();
        customerBob.firstName = "Bob";
        customerBob.lastName = "Wick";
        customerBob.login = "bob_wick@example.com";
        customerBob.pass = "bobpass";
        customerBob.balance = 0;

        Set<CustomerPojo> customers = new HashSet<>();
        customers.add(customerAlice);
        customers.add(customerBob);

        when(repository.getCustomers()).thenReturn(customers);
        CustomerPojo result = customerService.lookupCustomer(uuid);

        assertNull(result);
        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).getCustomer(customerAlice.id);
        verify(repository, times(0)).getCustomer(customerBob.id);
    }

    @Test
    void testLookUpCustomerByExistingLogin() {
        String login = "bob_wick@example.com";

        CustomerPojo customerAlice = new CustomerPojo();
        customerAlice.id = UUID.randomUUID();
        customerAlice.firstName = "Alice";
        customerAlice.lastName = "Wick";
        customerAlice.login = "alice_wick@example.com";
        customerAlice.pass = "alicepass";
        customerAlice.balance = 0;

        CustomerPojo customerBob = new CustomerPojo();
        customerBob.id = UUID.randomUUID();
        customerBob.firstName = "Bob";
        customerBob.lastName = "Wick";
        customerBob.login = login;
        customerBob.pass = "bobpass";
        customerBob.balance = 0;

        Set<CustomerPojo> customers = new HashSet<>();
        customers.add(customerAlice);
        customers.add(customerBob);

        when(repository.getCustomers()).thenReturn(customers);
        CustomerPojo result = customerService.lookupCustomer(login);

        assertEquals(customerBob, result);
        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).getCustomer(customerAlice.id);
        verify(repository, times(0)).getCustomer(customerBob.id);
    }

    @Test
    void testLookUpCustomerByNotExistingLogin() {
        String login = "guy_wick@example.com";

        CustomerPojo customerAlice = new CustomerPojo();
        customerAlice.id = UUID.randomUUID();
        customerAlice.firstName = "Alice";
        customerAlice.lastName = "Wick";
        customerAlice.login = "alice_wick@example.com";
        customerAlice.pass = "alicepass";
        customerAlice.balance = 0;

        CustomerPojo customerBob = new CustomerPojo();
        customerBob.id = UUID.randomUUID();
        customerBob.firstName = "Bob";
        customerBob.lastName = "Wick";
        customerBob.login = "bob_wick@example.com";
        customerBob.pass = "bobpass";
        customerBob.balance = 0;

        Set<CustomerPojo> customers = new HashSet<>();
        customers.add(customerAlice);
        customers.add(customerBob);

        when(repository.getCustomers()).thenReturn(customers);
        CustomerPojo result = customerService.lookupCustomer(login);

        assertNull(result);
        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).getCustomer(customerAlice.id);
        verify(repository, times(0)).getCustomer(customerBob.id);
    }

    @Test
    void testMeAsAdmin() {
        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails("UUID", "john_wick@example.com", Collections.singleton("ADMIN"));

        ContactPojo result = customerService.me(userDetails);
        assertEquals(Globals.ADMIN_LOGIN, result.login);
        verify(repository, times(0)).getCustomers();
        verify(repository, times(0)).getCustomerByLogin("john_wick@example.com");
    }

    @Test
    void testMeWhenWrongUserDetails() {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = UUID.randomUUID();
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "password";
        customerPojo.balance = 0;

        when(repository.getCustomerByLogin("john_wick@example.com")).thenReturn(customerPojo);

        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails("UUID", "john_wick@example.com", Collections.singleton("USER"));
        ContactPojo result = customerService.me(userDetails);

        assertEquals(0, result.balance);
        assertNotNull(result.lastName);
        assertNotNull(result.firstName);
        assertEquals(customerPojo.login, result.login);
        verify(repository, times(0)).getCustomers();
        verify(repository, times(0)).createCustomer(customerPojo);
        verify(repository, times(1)).getCustomerByLogin("john_wick@example.com");
    }

    @Test
    void testDeleteUserByUUID(){
        CustomerPojo customerToDelete = new CustomerPojo();
        customerToDelete.id = UUID.randomUUID();
        customerToDelete.firstName = "John";
        customerToDelete.lastName = "Wick";
        customerToDelete.login = "john_wick@example.com";
        customerToDelete.pass = "Baba_Jaga";
        customerToDelete.balance = 0;

        customerService.deleteCustomer(customerToDelete.id);
        verify(repository, times(1)).deleteCustomer(customerToDelete.id);
        verify(repository, times(0)).getCustomers();
        verify(repository, times(0)).createCustomer(customerToDelete);
    }

    @Test
    void testToUpBalance(){
        UUID uuid = UUID.randomUUID();
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = uuid;
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "Baba_Jaga";
        customerPojo.balance = 0;

        int money = 100;

        when(repository.getCustomer(uuid)).thenReturn(customerPojo);

        CustomerPojo result = customerService.topUpBalance(uuid, money);
        assertEquals(customerPojo.login, result.login);
        assertEquals(customerPojo.balance, result.balance);
        verify(repository, times(1)).editCustomer(customerPojo);
        verify(repository, times(1)).getCustomer(customerPojo.id);
        verify(repository, times(0)).getCustomers();
    }

}
