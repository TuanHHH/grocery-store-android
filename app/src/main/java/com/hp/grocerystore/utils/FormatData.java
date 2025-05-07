package com.hp.grocerystore.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatData {
    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(amount) + " đ";
    }
}
