package com.app.giftxchange.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Calendar;

public class ExpiryDateInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Allow backspace
        if (source.length() == 0) {
            return null;
        }

        StringBuilder input = new StringBuilder(dest);
        input.replace(dstart, dend, source.subSequence(start, end).toString());

        if (!isValidDate(input.toString())) {
            return "";
        }

        return null; // Accept the input
    }

    private boolean isValidDate(String input) {
        // Validate MM/YY format
        if (input.length() != 5) {
            return false;
        }

        try {
            int month = Integer.parseInt(input.substring(0, 2));
            int year = Integer.parseInt(input.substring(3));

            // Validate month and year
            if (month < 1 || month > 12 || year < Calendar.getInstance().get(Calendar.YEAR) % 100) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }

        return true;
    }
}
