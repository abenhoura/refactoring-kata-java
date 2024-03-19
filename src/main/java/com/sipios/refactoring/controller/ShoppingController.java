package com.sipios.refactoring.controller;

import com.sipios.refactoring.model.Body;
import com.sipios.refactoring.model.CustomerType;
import com.sipios.refactoring.model.Item;
import com.sipios.refactoring.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private Clock clock;

    @PostMapping
    public String getPrice(@RequestBody Body body) {

        if (body.getItems() == null) {
            return "0";
        }

        double result = 0;
        Calendar cal = getCalendar();
        var currentCustomerType = getCurrentCustomerType(body);

        if (
            !isDiscountPeriod(cal)
        ) {
            result = calculatePrice(body.getItems(), currentCustomerType, false);
        } else {
            result = calculatePrice(body.getItems(), currentCustomerType, true);
        }

        try {
            if (result > currentCustomerType.getThreshold()) {
                throw new Exception("Price (" + result + ") is too high for " + currentCustomerType.getType());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return String.valueOf(result);
    }

    private static double calculatePrice(Item[] items, CustomerType currentCustomerType, boolean applyDiscount) {
        double result = 0;
        for (int i = 0; i < items.length; i++) {
            Item it = items[i];
            var product = Product.valueOf(it.getType());
            var finalProduct = product.getPrice() * it.getNb() * currentCustomerType.getFactor();
            if (applyDiscount) {
                finalProduct = finalProduct * product.getDiscount();
            }
            result += finalProduct;
        }
        return result;
    }

    private Calendar getCalendar() {
        LocalDate localDate = LocalDate.now(clock);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        return cal;
    }

    private static boolean isDiscountPeriod(Calendar cal) {
        return (
            cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                cal.get(Calendar.MONTH) == 5
        ) ||
            (
                cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                    cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                    cal.get(Calendar.MONTH) == 0
            );
    }

    private CustomerType getCurrentCustomerType(Body b) {
        try {
            return CustomerType.valueOf(b.getType());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}

