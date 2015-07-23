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
package com.smartling.ml.model;

import com.smartling.ml.readers.ResourceReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader
{
    private final ClassLoader classLoader;

    public ResourceLoader()
    {
        this(ResourceLoader.class.getClassLoader());
    }

    public ResourceLoader(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public InputStream open(final String resourceName) throws IOException
    {
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);

        if (inputStream == null)
            throw new IOException(String.format("Resource not found: '%s'", resourceName));

        return inputStream;
    }

    public <T extends ResourceReader<R>, R> R readResource(final String resourceName, final T consumer) throws IOException
    {
        try (InputStream inputStream = open(resourceName))
        {
            return consumer.read(inputStream);
        }
    }

    public <T extends ResourceReader<R>, R> R readFile(final String fileName, final T consumer) throws IOException
    {
        try (InputStream inputStream = new FileInputStream(fileName))
        {
            return consumer.read(inputStream);
        }
    }
}
