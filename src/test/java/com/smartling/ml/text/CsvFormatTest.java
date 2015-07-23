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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvFormatTest
{
    private CsvFormat csv = new CsvFormat();

    @Test
    public void formatsDoubles()
    {
        String printed = csv.format(
                new double[]{0.0, 1.0 / 3, 2.5, -2.0, 9001.0, Double.NaN}
        );

        assertThat(printed, is("0,0.33333333,2.5,-2,9001,NaN"));
    }

    @Test
    public void formatsObjects()
    {
        String printed = csv.format("some", "str\"ing", "so,me", "te\nxt", 18.2 / 3, 1234, 2.8f / 3);

        assertThat(printed, is("some,\"str\"\"ing\",\"so,me\",\"te\nxt\",6.06666667,1234,0.93333334"));
    }
}
