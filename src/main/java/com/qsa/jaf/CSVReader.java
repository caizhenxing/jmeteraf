package com.qsa.jaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"UMAMAHESH.G"} on 11/03/15.
 */

public class CSVReader
{
    private BufferedReader br;
    private boolean hasNext = true;
    private char separator;
    private char quotechar;
    private int skipLines;
    private boolean linesSkiped;
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    public static final int DEFAULT_SKIP_LINES = 0;

    public CSVReader(Reader paramReader)
    {
        this(paramReader, ',');
    }

    public CSVReader(Reader paramReader, char paramChar)
    {
        this(paramReader, paramChar, '"');
    }

    public CSVReader(Reader paramReader, char paramChar1, char paramChar2)
    {
        this(paramReader, paramChar1, paramChar2, 0);
    }

    public CSVReader(Reader paramReader, char paramChar1, char paramChar2, int paramInt)
    {
        this.br = new BufferedReader(paramReader);
        this.separator = paramChar1;
        this.quotechar = paramChar2;
        this.skipLines = paramInt;
    }

    public List readAll()
            throws IOException
    {
        ArrayList localArrayList = new ArrayList();
        while (this.hasNext)
        {
            String[] arrayOfString = readNext();
            if (arrayOfString != null)
                localArrayList.add(arrayOfString);
        }
        return localArrayList;
    }

    public String[] readNext()
            throws IOException
    {
        String str = getNextLine();
        return this.hasNext ? parseLine(str) : null;
    }

    private String getNextLine()
            throws IOException
    {
        if (!this.linesSkiped)
        {
            for (int i = 0; i < this.skipLines; i++)
                this.br.readLine();
            this.linesSkiped = true;
        }
        String str = this.br.readLine();
        if (str == null)
            this.hasNext = false;
        return this.hasNext ? str : null;
    }

    private String[] parseLine(String paramString)
            throws IOException
    {
        if (paramString == null)
            return null;
        ArrayList localArrayList = new ArrayList();
        StringBuffer localStringBuffer = new StringBuffer();
        int i = 0;
        do
        {
            if (i != 0)
            {
                localStringBuffer.append("\n");
                paramString = getNextLine();
                if (paramString == null)
                    break;
            }
            for (int j = 0; j < paramString.length(); j++)
            {
                char c = paramString.charAt(j);
                if (c == this.quotechar)
                {
                    if ((i != 0) && (paramString.length() > j + 1) && (paramString.charAt(j + 1) == this.quotechar))
                    {
                        localStringBuffer.append(paramString.charAt(j + 1));
                        j++;
                    }
                    else
                    {
                        i = i == 0 ? 1 : 0;
                        if ((j <= 2) || (paramString.charAt(j - 1) == this.separator) || (paramString.length() <= j + 1) || (paramString.charAt(j + 1) == this.separator))
                            continue;
                        localStringBuffer.append(c);
                    }
                }
                else if ((c == this.separator) && (i == 0))
                {
                    localArrayList.add(localStringBuffer.toString());
                    localStringBuffer = new StringBuffer();
                }
                else
                {
                    localStringBuffer.append(c);
                }
            }
        }
        while (i != 0);
        localArrayList.add(localStringBuffer.toString());
        return (String[])(String[])localArrayList.toArray(new String[0]);
    }

    public void close()
            throws IOException
    {
        this.br.close();
    }
}

