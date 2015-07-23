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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CharsetDetectorTest
{
    private CharsetDetector detector = new CharsetDetector();

    @Test
    public void detectsUTF8() throws Exception
    {
        checkDetected(new byte[]{
                        (byte)0xef, (byte)0xbb, (byte)0xbf,
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xd1, (byte)0x97,
                        0x20, 0x74, 0x65, 0x78,
                        0x74
                }, "UTF-8"
        );

    }

    private void checkDetected(final byte[] bytes, final String expected) throws Exception
    {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes))
        {
            Optional<String> detected = detector.detect(inputStream);
            assertThat(detected, is(Optional.of(expected)));
        }
    }

    @Test
    public void detectsUTF8noBOM() throws Exception
    {
        checkDetected(new byte[]{
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xd1, (byte)0x97,
                        0x20, 0x74, 0x65, 0x78,
                        0x74, 0x20, 0x6e, 0x6f,
                        0x20, 0x62, 0x6f, 0x6d
                }, "UTF-8"
        );
    }

    @Test
    public void detectsUTF16LE() throws Exception
    {
        checkDetected(new byte[]{
                        (byte)0xff, (byte)0xfe,
                        0x73, 0, 0x6f, 0, 0x6d, 0, 0x65, 0,
                        0x20, 0, 0x57, 0x04, 0x20, 0,
                        0x74, 0, 0x65, 0, 0x78, 0, 0x74, 0
                }, "UTF-16LE"
        );
    }

    @Test
    public void detectsUTF16BE() throws Exception
    {
        checkDetected(new byte[]{
                        (byte)0xfe, (byte)0xff, 0,
                        0x73, 0, 0x6f, 0, 0x6d, 0, 0x65, 0,
                        0x20, 0, 0x57, 0x04, 0x20, 0,
                        0x74, 0, 0x65, 0, 0x78, 0, 0x74
                }, "UTF-16BE"
        );
    }

    @Test
    public void detectsIsoLatin() throws Exception
    {
        checkDetected(new byte[]{
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xe5, 0x20,
                        0x74, 0x65, 0x78, 0x74
                }, "ISO-8859-1"
        );
    }

    @Test
    public void preservesStreamPosition() throws Exception
    {
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        try (InputStream inputStream = new ByteArrayInputStream(bytes))
        {
            detector.detect(inputStream);

            assertThat(inputStream.available(), is(bytes.length));
        }
    }
}
