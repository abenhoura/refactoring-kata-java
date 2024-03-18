package com.sipios.refactoring.controller;

import com.sipios.refactoring.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;

class ShoppingControllerTests extends UnitTest {

    @InjectMocks
    private ShoppingController controller;

    @Mock
    private Clock clock;
    private static final ZonedDateTime NORMAL_PERIOD =
        ZonedDateTime.of(2023,2,15, 12,30,0,0, ZoneId.of("GMT"));

    private static final ZonedDateTime DISCOUNTS_PERIODS =
        ZonedDateTime.of(2023,6,6, 12,30,0,0, ZoneId.of("GMT"));

    @BeforeEach
    void setUp() {
        when(clock.getZone()).thenReturn(NORMAL_PERIOD.getZone());
        when(clock.instant()).thenReturn(NORMAL_PERIOD.toInstant());
    }

    @Test
    void shoudReturnZero() {
        //given
        //when
        var sut = controller.getPrice(new Body(new Item[]{}, "STANDARD_CUSTOMER"));
        //then
        Assertions.assertEquals("0.0", sut);
    }

    @Test
    void shoudReturn180WhenStandarCustomer() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        var sut = controller.getPrice(new Body(items, "STANDARD_CUSTOMER"));
        //then
        Assertions.assertEquals("180.0", sut);
    }


    @Test
    void shoudReturn175WhenPremiumCustomer() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        var sut = controller.getPrice(new Body(items, "PREMIUM_CUSTOMER"));
        //then
        Assertions.assertEquals("162.0", sut);
    }

    @Test
    void shoudReturn175WhenPremiumCustomerWith2Dress() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 2),
            new Item("DRESS", 3),
            new Item("JACKET", 1)
        };
        //when
        var sut = controller.getPrice(new Body(items, "PREMIUM_CUSTOMER"));
        //then
        Assertions.assertEquals("279.0", sut);
    }

    @Test
    void shoudReturn90WhenPlatiniumCustomer() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        var sut = controller.getPrice(new Body(items, "PLATINUM_CUSTOMER"));
        //then
        Assertions.assertEquals("90.0", sut);
    }

    @Test
    void shoudThrowExceptionWhenThresholdReachedStandardCustomer() {
        //given
        var items = new Item[]{
            new Item("JACKET", 11)
        };
        //then
        var thrown = Assertions.assertThrows(ResponseStatusException.class,
            ()->controller.getPrice(new Body(items, "STANDARD_CUSTOMER")));
        Assertions.assertEquals("Price (1100.0) is too high for standard customer", thrown.getReason());

    }

    @Test
    void shoudThrowExceptionWhenThresholdReachedPremiumCustomer() {
        //given
        var items = new Item[]{
            new Item("JACKET", 11)
        };
        //then
        var thrown = Assertions.assertThrows(ResponseStatusException.class,
            ()->controller.getPrice(new Body(items, "PREMIUM_CUSTOMER")));
        Assertions.assertEquals("Price (990.0) is too high for premium customer", thrown.getReason());

    }

    @Test
    void shoudThrowExceptionWhenThresholdReachedPlatinumCustomer() {
        //given
        var items = new Item[]{
            new Item("JACKET", 200)
        };
        //then
        var thrown = Assertions.assertThrows(ResponseStatusException.class,
            ()->controller.getPrice(new Body(items, "PLATINUM_CUSTOMER")));
        Assertions.assertEquals("Price (10000.0) is too high for platinum customer", thrown.getReason());

    }

    @Test
    void shoudReturn180WhenStandardCustomerAndDiscountPeriod() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        when(clock.instant()).thenReturn(DISCOUNTS_PERIODS.toInstant());
        var sut = controller.getPrice(new Body(items, "STANDARD_CUSTOMER"));
        //then
        Assertions.assertEquals("160.0", sut);
    }

    @Test
    void shoudReturn90WhenPlatiniumCustomerAndDiscountPeriod() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        when(clock.instant()).thenReturn(DISCOUNTS_PERIODS.toInstant());
        var sut = controller.getPrice(new Body(items, "PLATINUM_CUSTOMER"));
        //then
        Assertions.assertEquals("80.0", sut);
    }

    @Test
    void shoudReturn162WhenPremiumCustomerAndDiscountPeriod() {
        //given
        var items = new Item[]{
            new Item("TSHIRT", 1),
            new Item("DRESS", 1),
            new Item("JACKET", 1)
        };
        //when
        when(clock.instant()).thenReturn(DISCOUNTS_PERIODS.toInstant());
        var sut = controller.getPrice(new Body(items, "PREMIUM_CUSTOMER"));
        //then
        Assertions.assertEquals("144.0", sut);
    }
}
