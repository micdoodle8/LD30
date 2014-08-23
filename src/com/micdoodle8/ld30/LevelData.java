package com.micdoodle8.ld30;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelData
{
    public final char[][] charArray;

    public static LevelData read(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        List<String> lines = new ArrayList<String>();
        int maxLineSize = 0;

        while ((line = br.readLine()) != null)
        {
            maxLineSize = line.length(); // Lines are assumed to be the same size
            lines.add(line);
        }

        char[][] chars = new char[lines.size()][maxLineSize];

        for (int i = 0; i < lines.size(); i++)
        {
            String lineAt = lines.get(i);

            for (int j = 0; j < lineAt.length(); j++)
            {
                char charAt = lineAt.charAt(j);
                chars[lines.size() - i - 1][j] = charAt;
            }
        }

        return new LevelData(chars);
    }

    public LevelData(char[][] charArray)
    {
        this.charArray = charArray;
    }
}
