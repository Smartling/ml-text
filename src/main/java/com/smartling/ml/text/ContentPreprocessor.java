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

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ContentPreprocessor
{
    private static final int CONTENT_STRING_LIMIT = 2000;

    private final TextConverter textConverter;

    public ContentPreprocessor()
    {
        this(new TextConverter());
    }

    public ContentPreprocessor(TextConverter textConverter)
    {
        this.textConverter = textConverter;
    }

    public Optional<String> convert(InputStream inputStream) throws IOException
    {
        Optional<String> convertedString = textConverter.toString(inputStream);

        if (!convertedString.isPresent())
            return Optional.empty();

        String contentString = convertedString.get();
        contentString = contentString.substring(0, Math.min(CONTENT_STRING_LIMIT, contentString.length()));

        return Optional.of(contentString);
    }
}
