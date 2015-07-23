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
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextConverterTest
{
    @Mock
    private CharsetDetector charsetDetector;

    @Mock
    private InputStream inputStream;

    @Mock
    private InputStream ignore;

    @InjectMocks
    private TextConverter converter;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        withNoDetectedCharset();
    }

    private void withNoDetectedCharset() throws IOException
    {
        when(charsetDetector.detect(any()))
                .thenReturn(Optional.empty());
    }

    @Test
    public void callsCharsetDetector() throws Exception
    {
        converter.toString(inputStream);

        verify(charsetDetector).detect(inputStream);
    }

    @Test
    public void convertsNothingIfNoCharsetDetected() throws Exception
    {
        Optional<String> result = converter.toString(ignore);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void convertsASCII() throws Exception
    {
        withDetectedCharset("US-ASCII");

        checkConverted(new byte[]{
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, 0x74, 0x65, 0x78,
                        0x74
                }, "some text"
        );
    }

    private void withDetectedCharset(final String charset) throws IOException
    {
        when(charsetDetector.detect(any())).thenReturn(Optional.of(charset));
    }

    private void checkConverted(final byte[] bytes, final String expected) throws Exception
    {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes))
        {
            Optional<String> converted = converter.toString(inputStream);
            assertTrue(converted.isPresent());
            assertThat(converted.get().getBytes(), is(expected.getBytes()));
        }
    }

    @Test
    public void convertsUTF8() throws Exception
    {
        withDetectedCharset("UTF-8");

        checkConverted(new byte[]{
                        (byte)0xef, (byte)0xbb, (byte)0xbf,
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xd1, (byte)0x97,
                        0x20, 0x74, 0x65, 0x78,
                        0x74
                }, "some ї text"
        );
    }

    @Test
    public void convertsUTF8noBOM() throws Exception
    {
        withDetectedCharset("UTF-8");

        checkConverted(new byte[]{
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xd1, (byte)0x97,
                        0x20, 0x74, 0x65, 0x78,
                        0x74, 0x20, 0x6e, 0x6f,
                        0x20, 0x62, 0x6f, 0x6d
                }, "some ї text no bom"
        );
    }

    @Test
    public void convertsUTF16LE() throws Exception
    {
        withDetectedCharset("UTF-16LE");

        checkConverted(new byte[]{
                        (byte)0xff, (byte)0xfe,
                        0x73, 0, 0x6f, 0, 0x6d, 0, 0x65, 0,
                        0x20, 0, 0x57, 0x04, 0x20, 0,
                        0x74, 0, 0x65, 0, 0x78, 0, 0x74, 0
                }, "some ї text"
        );
    }

    @Test
    public void convertsUTF16BE() throws Exception
    {
        withDetectedCharset("UTF-16BE");

        checkConverted(new byte[]{
                        (byte)0xfe, (byte)0xff,
                        0, 0x73, 0, 0x6f, 0, 0x6d, 0, 0x65,
                        0, 0x20, 0x04, 0x57, 0, 0x20,
                        0, 0x74, 0, 0x65, 0, 0x78, 0, 0x74
                }, "some ї text"
        );
    }

    @Test
    public void convertsIsoLatin() throws Exception
    {
        withDetectedCharset("ISO-8859-1");

        checkConverted(new byte[]{
                        0x73, 0x6f, 0x6d, 0x65,
                        0x20, (byte)0xe5, 0x20,
                        0x74, 0x65, 0x78, 0x74
                }, "some å text"
        );
    }

    @Test
    public void doesNotConvertRandomBinary() throws Exception
    {
        withNoDetectedCharset();

        byte[] bytes = new byte[]{
                0, 1, 2, 3, 2, 1, 0, 0, 4, 6, 5
        };

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes))
        {
            Optional<String> converted = converter.toString(inputStream);
            assertThat(converted, is(Optional.empty()));
        }
    }
}
