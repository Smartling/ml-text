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
package com.smartling.ml.features;

import com.smartling.ml.readers.KeywordsReader;
import java.io.IOException;

/**
 * Loads keywords used in {@link FeatureExtractor}
 * <p>
 *     Default keywords are loaded from a resource named <b>ml.keywords</b>
 */
public class FeatureKeywords
{
    private static String[] defaultKeywords;

    public static String[] defaultKeywords()
    {
        if (defaultKeywords == null)
            defaultKeywords = loadDefaultKeywords();
        return defaultKeywords;
    }

    private static String[] loadDefaultKeywords()
    {
        try
        {
            return new KeywordsReader().fromResource("ml.keywords");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String[] fromFile(final String fileName) throws IOException
    {
        return new KeywordsReader().fromFile(fileName);
    }

    public String[] fromResource(final String resourceName) throws IOException
    {
        return new KeywordsReader().fromResource(resourceName);
    }

    public String[] fromResource(final ClassLoader classLoader, final String resourceName) throws IOException
    {
        return new KeywordsReader(classLoader).fromResource(resourceName);
    }
}
