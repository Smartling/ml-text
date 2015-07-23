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
package com.smartling.ml.readers;

import com.smartling.ml.model.ModelFormatException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LabelsReaderTest
{
    private LabelsReader reader;

    @Before
    public void setUp()
    {
        reader = new LabelsReader();
    }

    @Test
    public void loadsIndexed() throws IOException
    {
        String[] labels = loadLabels("0 some\n1 text");

        assertThat(labels[0], is("some"));
        assertThat(labels[1], is("text"));
    }

    private String[] loadLabels(final String resourceText) throws IOException
    {
        try (InputStream inputStream = IOUtils.toInputStream(resourceText))
        {
            return reader.read(inputStream);
        }
    }

    @Test(expected = ModelFormatException.class)
    public void expectsConsecutiveIndexes() throws IOException
    {
        loadLabels("0 some\n2 text");
    }
}
