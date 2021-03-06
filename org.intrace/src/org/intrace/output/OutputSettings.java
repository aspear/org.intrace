package org.intrace.output;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.intrace.shared.TraceConfigConstants;

public class OutputSettings
{
  private boolean stdoutOutputEnabled = false;
  private boolean fileOutputEnabled = false;
  private boolean netOutputEnabled = true;
  private File file1 = new File("trc1.txt");
  private File file2 = new File("trc2.txt");
  private PrintWriter file1TraceWriter = null;
  private PrintWriter file2TraceWriter = null;
  
  public boolean networkTraceOutputRequested = false;
  
  public OutputSettings(OutputSettings oldSettings)
  {
    stdoutOutputEnabled = oldSettings.stdoutOutputEnabled;
    fileOutputEnabled = oldSettings.fileOutputEnabled;
    netOutputEnabled = oldSettings.netOutputEnabled;
  }
  
  public OutputSettings(String args)
  {
    parseArgs(args);
  }

  public void parseArgs(String args)
  {
    String[] seperateArgs = args.split("\\[");
    for (int ii = 0; ii < seperateArgs.length; ii++)
    {
      parseArg("[" + seperateArgs[ii].toLowerCase(Locale.ROOT));
    }
  }

  private void parseArg(String arg)
  {
    if (arg.equals("[out-network"))
    {
      networkTraceOutputRequested = true;
    }
    else if (arg.equals(TraceConfigConstants.STD_OUT + "true"))
    {
      stdoutOutputEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.STD_OUT + "false"))
    {
      stdoutOutputEnabled = false;
    }
    else if (arg.equals(TraceConfigConstants.FILE_OUT + "true"))
    {
      fileOutputEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.FILE_OUT + "false"))
    {
      fileOutputEnabled = false;
      file1TraceWriter = closeFile(file1TraceWriter);
      file2TraceWriter = closeFile(file2TraceWriter);
    }
    else if (arg.startsWith("[out-file1-"))
    {
      String file1Name = arg.replace("[out-file1-", "");
      file1TraceWriter = closeFile(file1TraceWriter);
      file1 = new File(file1Name);
    }
    else if (arg.startsWith("[out-file2-"))
    {
      String file2Name = arg.replace("[out-file2-", "");
      file2TraceWriter = closeFile(file2TraceWriter);
      file2 = new File(file2Name);
    }
    else if (arg.equals(TraceConfigConstants.NET_OUT + "true"))
    {
      netOutputEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.NET_OUT + "false"))
    {
      System.setProperty("NET", "OFF");
      netOutputEnabled = false;
    }
  }
  

  public boolean isStdoutOutputEnabled()
  {
    return stdoutOutputEnabled;
  }

  public boolean isFileOutputEnabled()
  {
    return fileOutputEnabled;
  }
  
  public synchronized void writeFileOutput(String outputString)
  {
    PrintWriter outputWriter;
    outputWriter = getFileTraceWriter();
    if (outputWriter != null)
    {
      outputWriter.println(outputString);
      outputWriter.flush();
    }
  }

  public boolean isNetOutputEnabled()
  {
    return netOutputEnabled;
  }

  // Flag to indicate whether file output is currently going to file1 or file2
  private boolean file1Active = true;

  // Variable for tracking the number of bytes written to the output files
  private int writtenLines = 0;
  private static final int MAX_LINES_PER_FILE = 100 * 1000; // 100k lines

  public PrintWriter getFileTraceWriter()
  {
    // Handle rolling over between files
    writtenLines++;
    if (writtenLines > MAX_LINES_PER_FILE)
    {
      writtenLines = 0;
      file1Active = !file1Active;

      if (file1Active)
      {
        file1TraceWriter = resetFile(file1TraceWriter, file1, true);
      }
      else
      {
        file2TraceWriter = resetFile(file2TraceWriter, file2, true);
      }
    }
    if (file1Active)
    {
      if (file1TraceWriter == null)
      {
        file1TraceWriter = resetFile(file1TraceWriter, file1, false);
      }
      return file1TraceWriter;
    }
    else
    {
      if (file2TraceWriter == null)
      {
        file2TraceWriter = resetFile(file2TraceWriter, file2, false);
      }
      return file2TraceWriter;
    }
  }

  private PrintWriter closeFile(PrintWriter printWriter)
  {
    if (printWriter != null)
    {
      printWriter.flush();
      printWriter.close();
    }
    return null;
  }

  private PrintWriter resetFile(PrintWriter printWriter, File file,
                                boolean deleteFile)
  {
    writtenLines = 0;
    PrintWriter ret = null;
    try
    {
      closeFile(printWriter);
      if (deleteFile)
      {
        if (!file.delete())
        {
          System.err.println("InTrace failed to delete trace file: " + file.getAbsolutePath());
        }
      }
      else if (file.exists())
      {
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        try
        {
          while (reader.readLine() != null)
          {
            // Do nothing
          }
          writtenLines = reader.getLineNumber();
        }
        finally
        {
          reader.close();
        }
      }
      ret = new PrintWriter(new FileWriter(file, true));
    }
    catch (IOException e)
    {
      // Throw away
    }
    return ret;
  }

  public Map<String, String> getSettingsMap()
  {
    Map<String, String> settingsMap = new HashMap<String, String>();
    settingsMap.put(TraceConfigConstants.STD_OUT,
        Boolean.toString(stdoutOutputEnabled));
    settingsMap.put(TraceConfigConstants.FILE_OUT,
            Boolean.toString(fileOutputEnabled));
    settingsMap.put(TraceConfigConstants.NET_OUT,
            Boolean.toString(netOutputEnabled));
    return settingsMap;
  }
}
