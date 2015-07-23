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

import com.ibm.icu.text.CharsetMatch;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetDetector
{
    private static final Logger logger = LoggerFactory.getLogger(CharsetDetector.class);

    private static final int CONFIDENCE_THRESHOLD = 0;  // 0 - 100

    public Optional<String> detect(InputStream inputStream) throws IOException
    {
        IcuDetector icu = new IcuDetector();
        icu.setText(inputStream);
        CharsetMatch charsetMatch = icu.detect();

        logger.info("ICU: {} {}", charsetMatch.getConfidence(), charsetMatch.getName());

        if (charsetMatch.getConfidence() < CONFIDENCE_THRESHOLD)
            return Optional.empty();

        return Optional.ofNullable(charsetMatch.getName());
    }

    private class IcuDetector extends com.ibm.icu.text.CharsetDetector {}
}
