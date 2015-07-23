package com.smartling.ml.tools;

import com.smartling.ml.features.FeatureExtractor;
import com.smartling.ml.features.FeatureKeywords;
import com.smartling.ml.features.Features;
import com.smartling.ml.text.ContentPreprocessor;
import com.smartling.ml.text.CsvFormat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public class ExtractCommand
{
    private static final CsvFormat csvFormat = new CsvFormat();

    private final ContentPreprocessor preprocessor = new ContentPreprocessor();

    public void handle(final String[] args) throws IOException
    {
        String keywordsFile = args[0];
        String[] fileNames = Arrays.copyOfRange(args, 1, args.length);

        String[] keywords = FeatureKeywords.fromFile(keywordsFile);
        FeatureExtractor extractor = new FeatureExtractor(keywords);

        for (String fileName : fileNames)
        {
            String value = processFile(extractor, fileName);
            System.out.println(value);
        }
    }

    private String processFile(final FeatureExtractor extractor, final String fileName)
    {
        File file = new File(fileName);
        try (InputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferedStream = new BufferedInputStream(inputStream))
        {
            Optional<String> convertedString = preprocessor.convert(bufferedStream);

            String content = convertedString.isPresent() ? convertedString.get() : "";

            Features features = extractor.extract(content);

            return String.format("%s,%s", csvFormat.format(file.getName()), features.toCSV());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
