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

import com.smartling.ml.model.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.yaml.snakeyaml.Yaml;

public class KeywordsReader implements ResourceReader<String[]>
{
    private final ResourceLoader resourceLoader;

    public KeywordsReader()
    {
        this(new ResourceLoader());
    }

    public KeywordsReader(final ClassLoader classLoader)
    {
        this(new ResourceLoader(classLoader));
    }

    public KeywordsReader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String[] read(final InputStream inputStream) throws IOException
    {
        Yaml yaml = new Yaml();

        @SuppressWarnings("unchecked")
        List<String> strings = (List<String>)yaml.load(inputStream);

        return strings.stream().toArray(String[]::new);
    }

    public String[] fromFile(final String fileName) throws IOException
    {
        return resourceLoader.readFile(fileName, this);
    }

    public String[] fromResource(final String resourceName) throws IOException
    {
        return resourceLoader.readResource(resourceName, this);
    }
}
