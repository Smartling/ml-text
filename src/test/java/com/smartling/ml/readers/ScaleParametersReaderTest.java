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
import com.smartling.ml.model.ScaleParameters;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScaleParametersReaderTest
{
    private ScaleParametersReader reader;

    @Before
    public void setUp()
    {
        reader = new ScaleParametersReader();
    }

    @Test
    public void readsBounds() throws IOException
    {
        ScaleParameters scale = loadScaleParameters("x\n"
                        + "-10 19\n"
                        + "1 0 0"
        );

        assertThat(scale.lower, is(-10.0));
        assertThat(scale.upper, is(19.0));
    }

    private ScaleParameters loadScaleParameters(final String resourceText) throws IOException
    {
        try (InputStream inputStream = IOUtils.toInputStream(resourceText))
        {
            return reader.read(inputStream);
        }
    }

    @Test(expected = ModelFormatException.class)
    public void expectsXHeader() throws IOException
    {
        loadScaleParameters("y\n"
                        + "-10 19\n"
                        + "1 0 0"
        );
    }

    @Test
    public void readsZeroFeatures() throws IOException
    {
        ScaleParameters scale = loadScaleParameters("x\n"
                        + "-10 19\n"
        );

        assertThat(scale.feature_min[0], is(0.));
        assertThat(scale.feature_max[0], is(0.));
    }

    @Test
    public void readsFeatures() throws IOException
    {
        ScaleParameters scale = loadScaleParameters("x\n"
                        + "-10 19\n"
                        + "1 0.1 3.7\n"
                        + "2 3 8"
        );

        assertThat(scale.feature_min[1], is(0.1));
        assertThat(scale.feature_max[1], is(3.7));
        assertThat(scale.feature_min[2], is(3.));
        assertThat(scale.feature_max[2], is(8.));
    }

    @Test
    public void readsNonconsecutive() throws IOException
    {
        ScaleParameters scale = loadScaleParameters("x\n"
                        + "-10 19\n"
                        + "42 -0.99 0.3"
        );

        assertThat(scale.feature_min[42], is(-0.99));
        assertThat(scale.feature_max[42], is(0.3));
    }

    @Test(expected = ModelFormatException.class)
    public void readsBroken() throws IOException
    {
        loadScaleParameters("x\n"
                        + "-10 19\n"
                        + "42 -0.99\n"
        );
    }
}
