package com.micdoodle8.ld30;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LevelData
{
    public final char[][][] charArray;

    public static LevelData read(URL url) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        List<String> lines = new ArrayList<String>();
        int maxLineSize = 0;
        int layerCount = 1;

        while ((line = br.readLine()) != null)
        {
            if (line.equals(";"))
            {
                layerCount++;
            }
            else
            {
                maxLineSize = line.length(); // Lines are assumed to be the same size
                lines.add(line);
            }
        }

        char[][][] chars = new char[lines.size()][maxLineSize][layerCount];

        for (int i = 0; i < lines.size(); i++)
        {
            String lineAt = lines.get(i);

            for (int j = 0; j < lineAt.length(); j++)
            {
                char charAt = lineAt.charAt(j);
                chars[(lines.size() / layerCount) - (int)Math.floor(i % (lines.size() / layerCount)) - 1][j][(int)Math.floor(i / (lines.size() / layerCount))] = charAt;
            }
        }

        return new LevelData(chars);
    }

    public LevelData(char[][][] charArray)
    {
        this.charArray = charArray;
    }
}
