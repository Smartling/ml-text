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
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentPreprocessorTest
{
    @Mock
    private TextConverter textConverter;

    @Mock
    private InputStream inputStream;

    @Mock
    private InputStream ignore;

    @InjectMocks
    private ContentPreprocessor preprocessor;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(textConverter.toString(any())).thenReturn(Optional.of("setup string"));
    }

    @Test
    public void convertsContentToString() throws IOException
    {
        preprocessor.convert(inputStream);

        verify(textConverter).toString(inputStream);
    }

    @Test
    public void limitsConvertedText() throws Exception
    {
        String value = StringUtils.repeat('*', 2000) + StringUtils.repeat('-', 8000);
        when(textConverter.toString(any())).thenReturn(Optional.of(value));

        Optional<String> convertedString = preprocessor.convert(ignore);

        assertThat(convertedString, is(Optional.of(StringUtils.repeat('*', 2000))));
    }
}
