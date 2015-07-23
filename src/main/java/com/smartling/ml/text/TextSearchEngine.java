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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

/**
 * Optimized search of multiple substrings in one pass.
 */
public class TextSearchEngine
{
    private final Trie trie = new Trie();

    private final List<String> keywords = new ArrayList<>();

    public void addKeyword(String keyword)
    {
        trie.addKeyword(keyword);
        keywords.add(keyword);
    }

    /**
     * Counts number of each keyword found in the text.
     *
     * @param content input text
     * @return count of each keyword found, mapped by keyword
     */
    public Map<String, Integer> search(String content)
    {
        Map<String, Integer> result = zeroResult(keywords);

        Map<String, Integer> searchResult = trie
                .parseText(content)
                .stream()
                .map(Emit::getKeyword)
                .collect(
                        Collectors.groupingBy(
                                m -> m,
                                Collectors.reducing(0, e -> 1, Integer::sum)
                        )
                );

        result.putAll(searchResult);
        return result;
    }

    private Map<String, Integer> zeroResult(final List<String> keywords)
    {
        Map<String, Integer> result = new HashMap<>();
        for (String keyword : keywords)
            result.put(keyword, 0);
        return result;
    }
}
