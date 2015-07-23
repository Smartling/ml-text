/*
 *  Copyright 2015 Smartling, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this work except in compliance with the License.
 *  You may obtain a copy of the License in the LICENSE file, or at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.smartling.ml.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class CsvFormat
{
    private static final NumberFormat numberFormat = decimalFormatter();

    private static final Pattern UNSAFE_CHARS = Pattern.compile("\n|,|\"");

    public String format(double[] values)
    {
        return Arrays
                .stream(values)
                .mapToObj(numberFormat::format)
                .collect(Collectors.joining(","));
    }

    public String format(Object... values)
    {
        return Arrays
                .stream(values)
                .map(this::formatObject)
                .collect(Collectors.joining(","));
    }

    private String formatObject(Object value)
    {
        if (value == null)
            return "(null)";

        if (value instanceof String)
            return quotedString((String)value);

        if (value instanceof Double)
            return format(new double[]{(double)value});

        if (value instanceof Float)
            return format(new double[]{(double)(float)value});

        return value.toString();
    }

    private String quotedString(String value)
    {
        if (StringUtils.isEmpty(value))
            return "";

        if (UNSAFE_CHARS.matcher(value).find())
        {
            return String.format("\"%s\"", value.replace("\"", "\"\""));
        }

        return value;
    }

    private static NumberFormat decimalFormatter()
    {
        DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(8);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setNaN("NaN");
        format.setDecimalFormatSymbols(symbols);
        return format;
    }
}
