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
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class TextConverter
{
    private static final ByteOrderMark[] STRIP_BOMS = new ByteOrderMark[]{
            ByteOrderMark.UTF_8,
            ByteOrderMark.UTF_16BE,
            ByteOrderMark.UTF_16LE
    };

    private final CharsetDetector charsetDetector;

    public TextConverter()
    {
        this(new CharsetDetector());
    }

    public TextConverter(CharsetDetector charsetDetector)
    {
        this.charsetDetector = charsetDetector;
    }

    /**
     * Converts input stream to a string.
     * <p>
     *    Supported charsets:
     *    UTF-8, UTF-16, US-ASCII, and ISO-8859-1
     * @see <a href="http://docs.smartling.com/pages/API/">http://docs.smartling.com/pages/API/</a>
     * @param inputStream data stream to convert
     * @return <ul>
     *     <li>{@code Optional<String>} if converted successfully
     *     <li>{@code Optional.empty()} if conversion not available
     * </ul>
     * @throws IOException on read error
     */
    public Optional<String> toString(InputStream inputStream) throws IOException
    {
        Optional<String> encoding = charsetDetector.detect(inputStream);

        if (!encoding.isPresent())
            return Optional.empty();

        try (InputStream filteredStream = new BOMInputStream(inputStream, STRIP_BOMS))
        {
            String value = IOUtils.toString(filteredStream, encoding.get());
            return Optional.ofNullable(value);
        }
    }
}
