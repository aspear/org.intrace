package org.intrace.output.trace;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.intrace.shared.TraceConfigConstants;

/**
 * Args Format: "[arg1[arg2[arg3"
 * 
 * where argx is of the form value-parameter
 */
public class TraceSettings
{
  private boolean entryExitTraceEnabled = true;
  private boolean branchTraceEnabled = false;
  private boolean argTraceEnabled = true;
  private boolean truncateArraysEnabled = true;

  public TraceSettings(TraceSettings oldSettings)
  {
    entryExitTraceEnabled = oldSettings.entryExitTraceEnabled;
    branchTraceEnabled = oldSettings.branchTraceEnabled;
    argTraceEnabled = oldSettings.argTraceEnabled; 
    truncateArraysEnabled = oldSettings.truncateArraysEnabled;
  }

  public TraceSettings(String args)
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
    if (arg.equals(TraceConfigConstants.ENTRY_EXIT + "false"))
    {
      entryExitTraceEnabled = false;
    }
    else if (arg.equals(TraceConfigConstants.ENTRY_EXIT + "true"))
    {
      entryExitTraceEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.BRANCH + "true"))
    {
      branchTraceEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.BRANCH + "false"))
    {
      branchTraceEnabled = false;
    }
    else if (arg.equals(TraceConfigConstants.ARG + "true"))
    {
      argTraceEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.ARG + "false"))
    {
      argTraceEnabled = false;
    }
    else if (arg.equals(TraceConfigConstants.ARRAYS + "true"))
    {
      truncateArraysEnabled = true;
    }
    else if (arg.equals(TraceConfigConstants.ARRAYS + "false"))
    {
      truncateArraysEnabled = false;
    }
  }

  public boolean isEntryExitTraceEnabled()
  {
    return entryExitTraceEnabled;
  }

  public boolean isBranchTraceEnabled()
  {
    return branchTraceEnabled;
  }

  public boolean isArgTraceEnabled()
  {
    return argTraceEnabled;
  }
  
  public boolean isTruncateArraysEnabled()
  {
    return truncateArraysEnabled;
  }

  public Map<String, String> getSettingsMap()
  {
    Map<String, String> settingsMap = new HashMap<String, String>();
    settingsMap.put(TraceConfigConstants.ENTRY_EXIT,
                    Boolean.toString(entryExitTraceEnabled));
    settingsMap.put(TraceConfigConstants.BRANCH,
                    Boolean.toString(branchTraceEnabled));
    settingsMap
               .put(TraceConfigConstants.ARG, Boolean.toString(argTraceEnabled));
    settingsMap
    .put(TraceConfigConstants.ARRAYS, Boolean.toString(truncateArraysEnabled));
    return settingsMap;
  }
}