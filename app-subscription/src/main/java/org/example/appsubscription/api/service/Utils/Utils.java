package org.example.appsubscription.api.service.Utils;

import org.example.appsubscription.api.entity.User;

import java.time.LocalDate;
import java.util.Objects;

public class Utils {

    public static final int ONE_MONTH = 1;

    public static LocalDate addOneMonth(LocalDate date) {
        return date.plusMonths(ONE_MONTH);
    }

    public static void setPlusOneMonth(User user) {
        user.setEndDate(LocalDate.now().plusMonths(Utils.ONE_MONTH));
    }

    public static void addOneMonthEndDate(User user) {
        LocalDate currentEndDateUser = user.getEndDate();

        if (Objects.isNull(currentEndDateUser) || currentEndDateUser.isBefore(LocalDate.now())) {
            setPlusOneMonth(user);
        } else {
            LocalDate newEndDate = addOneMonth(currentEndDateUser);
            user.setEndDate(newEndDate);
        }
    }
}