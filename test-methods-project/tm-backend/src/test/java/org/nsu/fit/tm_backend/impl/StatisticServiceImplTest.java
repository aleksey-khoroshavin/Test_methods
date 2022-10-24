package org.nsu.fit.tm_backend.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nsu.fit.tm_backend.repository.data.CustomerPojo;
import org.nsu.fit.tm_backend.repository.data.SubscriptionPojo;
import org.nsu.fit.tm_backend.service.data.StatisticBO;
import org.nsu.fit.tm_backend.service.data.StatisticPerCustomerBO;
import org.nsu.fit.tm_backend.service.impl.CustomerServiceImpl;
import org.nsu.fit.tm_backend.service.impl.StatisticServiceImpl;
import org.nsu.fit.tm_backend.service.impl.SubscriptionServiceImpl;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

// Лабораторная 2: покрыть unit тестами класс StatisticServiceImpl на 100%.
// Чтобы протестировать метод calculate() используйте Mockito.spy(statisticService) и переопределите метод
// calculate(UUID customerId) чтобы использовать стратегию "разделяй и властвуй".
@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock
    private CustomerServiceImpl customerService;

    @Mock
    private SubscriptionServiceImpl subscriptionService;

    @InjectMocks
    private StatisticServiceImpl statisticService;


    @Test
    void calculateStatForAll(){
        StatisticPerCustomerBO statistic1 = new StatisticPerCustomerBO();
        UUID customerId1 = UUID.randomUUID();
        statistic1.setCustomerId(customerId1);
        statistic1.setOverallFee(50);
        statistic1.setOverallBalance(100);

        Set<UUID> customer1Subs = new HashSet<>();
        customer1Subs.add(UUID.randomUUID());
        customer1Subs.add(UUID.randomUUID());

        statistic1.setSubscriptionIds(customer1Subs);

        StatisticPerCustomerBO statistic2 = new StatisticPerCustomerBO();
        UUID customerId2 = UUID.randomUUID();
        statistic2.setCustomerId(customerId2);
        statistic2.setOverallFee(254);
        statistic2.setOverallBalance(56);

        Set<UUID> customer2Subs = new HashSet<>();
        customer2Subs.add(UUID.randomUUID());
        customer2Subs.add(UUID.randomUUID());

        statistic2.setSubscriptionIds(customer2Subs);

        UUID notExistingCustomerId = UUID.randomUUID();

        Set<UUID> customerIds = new HashSet<>();
        customerIds.add(customerId1);
        customerIds.add(customerId2);
        customerIds.add(notExistingCustomerId);

        when(customerService.getCustomerIds()).thenReturn(customerIds);

        StatisticServiceImpl service = Mockito.spy(statisticService);
        Mockito.doReturn(statistic1).when(service).calculate(customerId1);
        Mockito.doReturn(statistic2).when(service).calculate(customerId2);
        Mockito.doReturn(null).when(service).calculate(notExistingCustomerId);

        StatisticBO statisticBO = service.calculate();

        assertNotNull(statisticBO);
        assertEquals(2, statisticBO.getCustomers().size());
        assertEquals(304, statisticBO.getOverallFee());
        assertEquals(156, statisticBO.getOverallBalance());
    }

    @Test
    void calculateStatForExistingCustomer(){
        UUID existingCustomerId = UUID.randomUUID();
        CustomerPojo existingCustomerPojo = new CustomerPojo();
        existingCustomerPojo.id = existingCustomerId;
        existingCustomerPojo.firstName = "John";
        existingCustomerPojo.lastName = "Wick";
        existingCustomerPojo.login = "john_wick@example.com";
        existingCustomerPojo.pass = "Baba_Jaga";
        existingCustomerPojo.balance = 0;

        when(customerService.lookupCustomer(existingCustomerId)).thenReturn(existingCustomerPojo);

        SubscriptionPojo subscriptionPojo1 = new SubscriptionPojo();
        subscriptionPojo1.id = UUID.randomUUID();
        subscriptionPojo1.customerId = existingCustomerId;
        subscriptionPojo1.planFee = 15;

        SubscriptionPojo subscriptionPojo2 = new SubscriptionPojo();
        subscriptionPojo2.id = UUID.randomUUID();
        subscriptionPojo2.customerId = existingCustomerId;
        subscriptionPojo2.planFee = 20;

        List<SubscriptionPojo> subscriptions = new ArrayList<>();
        subscriptions.add(subscriptionPojo1);
        subscriptions.add(subscriptionPojo2);

        when(subscriptionService.getSubscriptions(existingCustomerId)).thenReturn(subscriptions);

        StatisticPerCustomerBO statistic = statisticService.calculate(existingCustomerId);
        assertNotNull(statistic);
        assertEquals(2, statistic.getSubscriptionIds().size());
        assertEquals(35, statistic.getOverallFee());
        assertEquals(0, statistic.getOverallBalance());
    }

    @Test
    void calculateStatForNotExistingCustomer(){
        UUID notExistingCustomerId = UUID.randomUUID();
        when(customerService.lookupCustomer(notExistingCustomerId)).thenReturn(null);
        StatisticPerCustomerBO statistic = statisticService.calculate(notExistingCustomerId);
        assertNull(statistic);
    }
}
