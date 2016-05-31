/**
 * com.github.lecogiteur.csvbang.util.CsvBangNumberFormat
 * <p>
 * Copyright (C) 2013-2016  Tony EMMA
 * <p>
 * This file is part of Csvbang.
 * <p>
 * Csvbang is a comma-separated values ( CSV ) API, written in JAVA and thread-safe.
 * <p>
 * Csvbang is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * <p>
 * Csvbang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Csvbang. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lecogiteur.csvbang.util;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by lecogiteur on 29/05/16.
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsvBangNumberFormat extends ThreadLocal<DecimalFormat> {

    /**
     * Pattern
     * @since 1.0.0
     */
    private final String pattern;

    /**
     * Locale
     * @since 1.0.0
     */
    private final Locale locale;

    private Currency currency;

    private Integer minimumIntegerDigits;

    private Integer maximumIntegerDigits;

    private Boolean parseIntegerOnly;

    private Integer maximumFractionDigits;

    private Integer minimumFractionDigits;

    /**
     * Constructor
     * @param pattern pattern
     * @param locale Locale 
     * @since 1.0.0
     */
    public CsvBangNumberFormat(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DecimalFormat initialValue() {
        final DecimalFormat format = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        if (CsvbangUti.isStringNotBlank(pattern)){
            format.applyPattern(pattern);
        }
        format.setParseBigDecimal(true);

        if (currency != null){
            format.setCurrency(currency);
        }

        if (minimumIntegerDigits != null) {
            format.setMinimumIntegerDigits(minimumIntegerDigits);
        }

        if (maximumIntegerDigits != null){
            format.setMaximumIntegerDigits(maximumIntegerDigits);
        }

        if (parseIntegerOnly != null){
            format.setParseIntegerOnly(parseIntegerOnly);
        }

        if (maximumFractionDigits != null){
            format.setMaximumFractionDigits(maximumFractionDigits);
        }

        if (minimumFractionDigits != null){
            format.setMinimumFractionDigits(minimumFractionDigits);
        }

        return format;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        remove();
    }

    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
        remove();
    }

    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
        remove();
    }

    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
        remove();
    }

    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
        remove();
    }

    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
        remove();
    }
}
