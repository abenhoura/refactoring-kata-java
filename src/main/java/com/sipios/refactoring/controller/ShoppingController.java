package com.sipios.refactoring.controller;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.sipios.refactoring.model.Body;
import com.sipios.refactoring.model.CustomerType;
import com.sipios.refactoring.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private Clock clock;

    @PostMapping
    public String getPrice(@RequestBody Body b) {

        if (b.getItems() == null) {
            return "0";
        }

        double result = 0;
        Calendar cal = getCalendar();
        var currentCustomerType = getCurrentCustomerType(b);

        if (
            !isDiscountPeriod(cal)
        ) {
            for (int i = 0; i < b.getItems().length; i++) {
                Item it = b.getItems()[i];

                if (it.getType().equals("TSHIRT")) {
                    result += 30 * it.getNb() * currentCustomerType.getFactor();
                } else if (it.getType().equals("DRESS")) {
                    result += 50 * it.getNb() * currentCustomerType.getFactor();
                } else if (it.getType().equals("JACKET")) {
                    result += 100 * it.getNb() * currentCustomerType.getFactor();
                }

            }
        } else {
            if (b.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < b.getItems().length; i++) {
                Item it = b.getItems()[i];

                if (it.getType().equals("TSHIRT")) {
                    result += 30 * it.getNb() * currentCustomerType.getFactor();
                } else if (it.getType().equals("DRESS")) {
                    result += 50 * it.getNb() * 0.8 * currentCustomerType.getFactor();
                } else if (it.getType().equals("JACKET")) {
                    result += 100 * it.getNb() * 0.9 * currentCustomerType.getFactor();
                }
            }
        }

        try {
            if (b.getType().equals("STANDARD_CUSTOMER")) {
                if (result > 200) {
                    throw new Exception("Price (" + result + ") is too high for standard customer");
                }
            } else if (b.getType().equals("PREMIUM_CUSTOMER")) {
                if (result > 800) {
                    throw new Exception("Price (" + result + ") is too high for premium customer");
                }
            } else if (b.getType().equals("PLATINUM_CUSTOMER")) {
                if (result > 2000) {
                    throw new Exception("Price (" + result + ") is too high for platinum customer");
                }
            } else {
                if (result > 200) {
                    throw new Exception("Price (" + result + ") is too high for standard customer");
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return String.valueOf(result);
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

