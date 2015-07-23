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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LabelsReader implements ResourceReader<String[]>
{
    private final ResourceLoader resourceLoader;

    public LabelsReader()
    {
        this(new ResourceLoader());
    }

    public LabelsReader(final ClassLoader classLoader)
    {
        this(new ResourceLoader(classLoader));
    }

    public LabelsReader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String[] read(final InputStream inputStream) throws IOException
    {
        try (InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputReader))
        {
            return readLabels(reader);
        }
    }

    public String[] fromFile(final String fileName) throws IOException
    {
        return resourceLoader.readFile(fileName, this);
    }

    public String[] fromResource(final String resourceName) throws IOException
    {
        return resourceLoader.readResource(resourceName, this);
    }

    private static String[] readLabels(final BufferedReader reader) throws IOException
    {
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (String line = reader.readLine(); line != null; line = reader.readLine(), index++)
        {
            String[] row = line.split(" ");
            if (Integer.valueOf(row[0]) != index)
                throw new ModelFormatException(String.format("Unexpected labels row: '%s'", line));
            labels.add(row[1]);
        }

        return labels.stream().toArray(String[]::new);
    }
}
