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
import com.smartling.ml.model.ResourceLoader;
import com.smartling.ml.model.ScaleParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScaleParametersReader implements ResourceReader<ScaleParameters>
{
    private final ResourceLoader resourceLoader;

    public ScaleParametersReader()
    {
        this(new ResourceLoader());
    }

    public ScaleParametersReader(final ClassLoader classLoader)
    {
        this(new ResourceLoader(classLoader));
    }

    public ScaleParametersReader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ScaleParameters read(final InputStream inputStream) throws IOException
    {
        try (InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputReader))
        {
            return readScaleParameters(reader);
        }

    }

    public ScaleParameters fromFile(final String fileName) throws IOException
    {
        return resourceLoader.readFile(fileName, this);
    }

    public ScaleParameters fromResource(final String resourceName) throws IOException
    {
        return resourceLoader.readResource(resourceName, this);
    }

    private ScaleParameters readScaleParameters(final BufferedReader reader) throws IOException
    {
        ScaleParameters params = new ScaleParameters();

        String header = reader.readLine();
        if (!header.equals("x"))
            throw new ModelFormatException("Unexpected scaling data format");

        double[] row = readLineOfDoubles(reader);
        params.lower = row[0];
        params.upper = row[1];

        class feature
        {
            int index;
            double min;
            double max;

            feature(int index, double min, double max)
            {
                this.index = index;
                this.min = min;
                this.max = max;
            }
        }

        List<feature> features = new ArrayList<>();

        for (row = readLineOfDoubles(reader); row != null; row = readLineOfDoubles(reader))
        {
            if (row.length < 3)
                throw new ModelFormatException(String.format("Unexpected scale row: %s", Arrays.toString(row)));

            features.add(new feature((int)row[0], row[1], row[2]));
        }

        int maxIndex = features
                .stream()
                .mapToInt(x -> x.index)
                .max()
                .orElse(0);

        double[] feature_max = new double[maxIndex + 1];
        double[] feature_min = new double[maxIndex + 1];

        features.forEach(x -> {
                    feature_max[x.index] = x.max;
                    feature_min[x.index] = x.min;
                }
        );

        params.feature_max = feature_max;
        params.feature_min = feature_min;

        return params;
    }

    private static double[] readLineOfDoubles(final BufferedReader reader) throws IOException
    {
        String line = reader.readLine();
        if (line == null)
            return null;
        return Arrays
                .stream(line.split(" "))
                .mapToDouble(Double::valueOf)
                .toArray();
    }
}
