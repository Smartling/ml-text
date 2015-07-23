package com.smartling.ml.tools;

import java.io.IOException;
import java.util.Arrays;

public class CommandProcessor
{
    private static final String USAGE = "usage: ./processor extract <keywords.yml> <file> ...";

    private static final String EXTRACT_COMMAND = "extract";

    public void processCommand(final String[] args) throws IOException
    {
        if (args.length < 2)
        {
            printUsage();
            return;
        }

        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        if (command.equals(EXTRACT_COMMAND))
        {
            new ExtractCommand().handle(commandArgs);
        }
        else
        {
            System.err.println("! unknown command: " + command);
            printUsage();
        }
    }

    private static void printUsage()
    {
        System.out.println(USAGE);
    }

}
