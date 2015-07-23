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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import libsvm.svm;

public class SvmModelReader implements ResourceReader<Object>
{
    private final ResourceLoader resourceLoader;

    public SvmModelReader()
    {
        this(new ResourceLoader());
    }

    public SvmModelReader(final ClassLoader classLoader)
    {
        this(new ResourceLoader(classLoader));
    }

    public SvmModelReader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Object read(final InputStream inputStream) throws IOException
    {
        try (InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputReader))
        {
            return svm.svm_load_model(reader);
        }

    }

    public Object fromFile(final String fileName) throws IOException
    {
        return resourceLoader.readFile(fileName, this);
    }

    public Object fromResource(final String resourceName) throws IOException
    {
        return resourceLoader.readResource(resourceName, this);
    }

}
