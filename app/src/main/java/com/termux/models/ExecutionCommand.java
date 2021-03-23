package com.termux.models;

import android.app.PendingIntent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.termux.app.utils.Logger;
import com.termux.app.utils.MarkdownUtils;
import com.termux.app.utils.TextDataUtils;

import java.util.ArrayList;
import java.util.List;

public class ExecutionCommand {

    /*
    The {@link ExecutionState#SUCCESS} and {@link ExecutionState#FAILED} is defined based on
    successful execution of command without any internal errors or exceptions being raised.
    The shell command {@link #exitCode} being non-zero **does not** mean that execution command failed.
    Only the {@link #errCode} being non-zero means that execution command failed from the Termux app
    perspective.
    */

    /** The {@link Enum} that defines {@link ExecutionCommand} state. */
    public enum ExecutionState {

        PRE_EXECUTION("Pre-Execution", 0),
        EXECUTING("Executing", 1),
        EXECUTED("Executed", 2),
        SUCCESS("Success", 3),
        FAILED("Failed", 4);

        private final String name;
        private final int value;

        ExecutionState(final String name, final int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

    }

    /** The optional unique id for the {@link ExecutionCommand}. */
    public Integer id;


    /** The current state of the {@link ExecutionCommand}. */
    public ExecutionState currentState = ExecutionState.PRE_EXECUTION;
    /** The previous state of the {@link ExecutionCommand}. */
    public ExecutionState previousState = ExecutionState.PRE_EXECUTION;


    /** The executable for the {@link ExecutionCommand}. */
    public String executable;
    /** The executable Uri for the {@link ExecutionCommand}. */
    public Uri executableUri;
    /** The executable arguments array for the {@link ExecutionCommand}. */
    public String[] arguments;
    /** The current working directory for the {@link ExecutionCommand}. */
    public String workingDirectory;


    /** If the {@link ExecutionCommand} is a background or a foreground terminal session command. */
    public boolean inBackground;
    /** If the {@link ExecutionCommand} is meant to start a failsafe terminal session. */
    public boolean isFailsafe;


    /** The session action of foreground commands. */
    public String sessionAction;


    /** The command label for the {@link ExecutionCommand}. */
    public String commandLabel;
    /** The markdown text for the command description for the {@link ExecutionCommand}. */
    public String commandDescription;
    /** The markdown text for the help of command for the {@link ExecutionCommand}. This can be used
     * to provide useful info to the user if an internal error is raised. */
    public String commandHelp;


    /** Defines the markdown text for the help of the Termux plugin API that was used to start the
     * {@link ExecutionCommand}. This can be used to provide useful info to the user if an internal
     * error is raised. */
    public String pluginAPIHelp;


    /** Defines if {@link ExecutionCommand} was started because of an external plugin request or from
     * within Termux app itself. */
    public boolean isPluginExecutionCommand;
    /** Defines {@link PendingIntent} that should be sent if an external plugin requested the execution. */
    public PendingIntent pluginPendingIntent;


    /** The stdout of shell command. */
    public String stdout;
    /** The sterr of shell command. */
    public String stderr;
    /** The exit code of shell command. */
    public Integer exitCode;


    /** The internal error code of {@link ExecutionCommand}. */
    public Integer errCode;
    /** The internal error message of {@link ExecutionCommand}. */
    public String errmsg;
    /** The internal exceptions of {@link ExecutionCommand}. */
    public List<Throwable> throwableList  = new ArrayList<>();



    public ExecutionCommand(){
    }

    public ExecutionCommand(Integer id){
        this.id = id;
    }

    public ExecutionCommand(Integer id, String executable, String[] arguments, String workingDirectory, boolean inBackground, boolean isFailsafe) {
        this.id = id;
        this.executable = executable;
        this.arguments = arguments;
        this.workingDirectory = workingDirectory;
        this.inBackground = inBackground;
        this.isFailsafe = isFailsafe;
    }

    @NonNull
    @Override
    public String toString() {
        if(currentState.getValue() < ExecutionState.EXECUTED.getValue())
            return getExecutionInputLogString(this, true);
        else {
            return getExecutionOutputLogString(this, true);
        }
    }

    /**
     * Get a log friendly {@link String} for {@link ExecutionCommand} execution input parameters.
     *
     * @param executionCommand The {@link ExecutionCommand} to convert.
     * @param ignoreNull Set to {@code true} if non-critical {@code null} values are to be ignored.
     * @return Returns the log friendly {@link String}.
     */
    public static String getExecutionInputLogString(ExecutionCommand executionCommand, boolean ignoreNull) {
        if (executionCommand == null) return "null";

        StringBuilder logString = new StringBuilder();

        logString.append(executionCommand.getIdLogString());
        logString.append(executionCommand.getCommandLabelLogString()).append(":");

        if(executionCommand.previousState != ExecutionState.PRE_EXECUTION)
            logString.append("\n").append(executionCommand.getPreviousStateLogString());
        logString.append("\n").append(executionCommand.getCurrentStateLogString());

        logString.append("\n").append(executionCommand.getExecutableLogString());
        logString.append("\n").append(executionCommand.getArgumentsLogString());
        logString.append("\n").append(executionCommand.getWorkingDirectoryLogString());
        logString.append("\n").append(executionCommand.getInBackgroundLogString());
        logString.append("\n").append(executionCommand.getIsFailsafeLogString());


        if(!ignoreNull || executionCommand.sessionAction != null)
            logString.append("\n").append(executionCommand.getSessionActionLogString());

        logString.append("\n").append(executionCommand.getIsPluginExecutionCommandLogString());
        if(!ignoreNull || executionCommand.isPluginExecutionCommand) {
            if (!ignoreNull || executionCommand.pluginPendingIntent != null)
                logString.append("\n").append(executionCommand.getPendingIntentCreatorLogString());
        }

        return logString.toString();
    }

    /**
     * Get a log friendly {@link String} for {@link ExecutionCommand} execution output parameters.
     *
     * @param executionCommand The {@link ExecutionCommand} to convert.
     * @param ignoreNull Set to {@code true} if non-critical {@code null} values are to be ignored.
     * @return Returns the log friendly {@link String}.
     */
    public static String getExecutionOutputLogString(ExecutionCommand executionCommand, boolean ignoreNull) {
        if (executionCommand == null) return "null";

        StringBuilder logString = new StringBuilder();

        logString.append(executionCommand.getIdLogString());
        logString.append(executionCommand.getCommandLabelLogString()).append(":");

        logString.append("\n").append(executionCommand.getPreviousStateLogString());
        logString.append("\n").append(executionCommand.getCurrentStateLogString());

        logString.append("\n").append(executionCommand.getStdoutLogString());
        logString.append("\n").append(executionCommand.getStderrLogString());
        logString.append("\n").append(executionCommand.getExitCodeLogString());

        logString.append(getExecutionErrLogString(executionCommand, ignoreNull));

        return logString.toString();
    }

    /**
     * Get a log friendly {@link String} for {@link ExecutionCommand} execution error parameters.
     *
     * @param executionCommand The {@link ExecutionCommand} to convert.
     * @param ignoreNull Set to {@code true} if non-critical {@code null} values are to be ignored.
     * @return Returns the log friendly {@link String}.
     */
    public static String getExecutionErrLogString(ExecutionCommand executionCommand, boolean ignoreNull) {
        StringBuilder logString = new StringBuilder();

        if(!ignoreNull || (executionCommand.errCode != null && executionCommand.errCode != 0)) {
            logString.append("\n").append(executionCommand.getErrCodeLogString());
            logString.append("\n").append(executionCommand.getErrmsgLogString());
            logString.append("\n").append(executionCommand.geStackTracesLogString());
        } else {
            logString.append("");
        }

        return logString.toString();
    }

    /**
     * Get a log friendly {@link String} for {@link ExecutionCommand} with more details.
     *
     * @param executionCommand The {@link ExecutionCommand} to convert.
     * @return Returns the log friendly {@link String}.
     */
    public static String getDetailedLogString(ExecutionCommand executionCommand) {
        if (executionCommand == null) return "null";

        StringBuilder logString = new StringBuilder();

        logString.append(getExecutionInputLogString(executionCommand, false));
        logString.append(getExecutionOutputLogString(executionCommand, false));

        logString.append("\n").append(executionCommand.getCommandDescriptionLogString());
        logString.append("\n").append(executionCommand.getCommandHelpLogString());
        logString.append("\n").append(executionCommand.getPluginAPIHelpLogString());

        return logString.toString();
    }

    /**
     * Get a markdown {@link String} for {@link ExecutionCommand}.
     *
     * @param executionCommand The {@link ExecutionCommand} to convert.
     * @return Returns the markdown {@link String}.
     */
    public static String getDetailedMarkdownString(ExecutionCommand executionCommand) {
        if (executionCommand == null) return "null";

        if (executionCommand.commandLabel == null) executionCommand.commandLabel = "Execution Command";

        StringBuilder markdownString = new StringBuilder();

        markdownString.append("### ").append(executionCommand.commandLabel).append("\n");


        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Previous State", executionCommand.previousState.getName(), "-"));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Current State", executionCommand.currentState.getName(), "-"));

        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Executable", executionCommand.executable, "-"));
        markdownString.append("\n").append(getArgumentsMarkdownString(executionCommand.arguments));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Working Directory", executionCommand.workingDirectory, "-"));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("inBackground", executionCommand.inBackground, "-"));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("isFailsafe", executionCommand.isFailsafe, "-"));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Session Action", executionCommand.sessionAction, "-"));


        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("isPluginExecutionCommand", executionCommand.isPluginExecutionCommand, "-"));
        if (executionCommand.pluginPendingIntent != null)
            markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Pending Intent Creator", executionCommand.pluginPendingIntent.getCreatorPackage(), "-"));
        else
            markdownString.append("\n").append("**Pending Intent Creator:** -  ");

        markdownString.append("\n\n").append(MarkdownUtils.getMultiLineMarkdownStringEntry("Stdout", executionCommand.stdout, "-"));
        markdownString.append("\n").append(MarkdownUtils.getMultiLineMarkdownStringEntry("Stderr", executionCommand.stderr, "-"));
        markdownString.append("\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Exit Code", executionCommand.exitCode, "-"));

        markdownString.append("\n\n").append(MarkdownUtils.getSingleLineMarkdownStringEntry("Err Code", executionCommand.errCode, "-"));
        markdownString.append("\n").append("**Errmsg:**\n").append(TextDataUtils.getDefaultIfNull(executionCommand.errmsg, "-"));
        markdownString.append("\n\n").append(executionCommand.geStackTracesMarkdownString());

        if(executionCommand.commandDescription != null || executionCommand.commandHelp != null) {
            if (executionCommand.commandDescription != null)
                markdownString.append("\n\n#### Command Description\n\n").append(executionCommand.commandDescription).append("\n");
            if (executionCommand.commandHelp != null)
                markdownString.append("\n\n#### Command Help\n\n").append(executionCommand.commandHelp).append("\n");
            markdownString.append("\n##\n");
        }

        if(executionCommand.pluginAPIHelp != null) {
            markdownString.append("\n\n#### Plugin API Help\n\n").append(executionCommand.pluginAPIHelp);
            markdownString.append("\n##\n");
        }

        return markdownString.toString();
    }



    public String getIdLogString() {
        if(id != null)
            return "(" + id + ") ";
        else
            return "";
    }

    public String getCurrentStateLogString() {
        return "Current State: `" + currentState.getName() + "`";
    }

    public String getPreviousStateLogString() {
        return "Previous State: `" + previousState.getName() + "`";
    }

    public String getCommandLabelLogString() {
        if (commandLabel != null && !commandLabel.isEmpty())
            return commandLabel;
        else
            return "Execution Command";
    }

    public String getExecutableLogString() {
        return "Executable: `" + executable + "`";
    }

    public String getArgumentsLogString() {
        return getArgumentsLogString(arguments);
    }

    public String getWorkingDirectoryLogString() {
        return "Working Directory: `" + workingDirectory + "`";
    }

    public String getInBackgroundLogString() {
        return "inBackground: `" + inBackground + "`";
    }

    public String getIsFailsafeLogString() {
        return "isFailsafe: `" + isFailsafe + "`";
    }

    public String getIsPluginExecutionCommandLogString() {
        return "isPluginExecutionCommand: `" + isPluginExecutionCommand + "`";
    }

    public String getSessionActionLogString() {
        return getSingleLineLogStringEntry("Session Action", sessionAction, "-");
    }

    public String getPendingIntentCreatorLogString() {
        if (pluginPendingIntent != null)
            return "Pending Intent Creator: `" + pluginPendingIntent.getCreatorPackage() + "`";
        else
            return "Pending Intent Creator: -";
    }

    public String getCommandDescriptionLogString() {
        return getSingleLineLogStringEntry("Command Description", commandDescription, "-");
    }

    public String getCommandHelpLogString() {
        return getSingleLineLogStringEntry("Command Help", commandHelp, "-");
    }

    public String getPluginAPIHelpLogString() {
        return getSingleLineLogStringEntry("Plugin API Help", pluginAPIHelp, "-");
    }

    public String getStdoutLogString() {
        return getMultiLineLogStringEntry("Stdout", stdout, "-");
    }

    public String getStderrLogString() {
        return getMultiLineLogStringEntry("Stderr", stderr, "-");
    }

    public String getExitCodeLogString() {
        return getSingleLineLogStringEntry("Exit Code", exitCode, "-");
    }

    public String getErrCodeLogString() {
        return getSingleLineLogStringEntry("Err Code", errCode, "-");
    }

    public String getErrmsgLogString() {
        return getMultiLineLogStringEntry("Errmsg", errmsg, "-");
    }

    public String geStackTracesLogString() {
        return Logger.getStackTracesString("StackTraces:", Logger.getStackTraceStringArray(throwableList));
    }

    public String geStackTracesMarkdownString() {
        return Logger.getStackTracesMarkdownString("StackTraces:", Logger.getStackTraceStringArray(throwableList));
    }



    /**
     * Get a markdown {@link String} for {@link String[]} argumentsArray.
     * If argumentsArray are null or of size 0, then `**Arguments:** -` is returned. Otherwise
     * following format is returned:
     *
     * **Arguments:**
     *
     * **Arg 1:**
     * ```
     * value
     * ```
     * **Arg 2:**
     * ```
     * value
     *```
     *
     * @param argumentsArray The {@link String[]} argumentsArray to convert.
     * @return Returns the markdown {@link String}.
     */
    public static String getArgumentsMarkdownString(String[] argumentsArray) {
        StringBuilder argumentsString = new StringBuilder("**Arguments:**");

        if (argumentsArray != null && argumentsArray.length != 0) {
            argumentsString.append("\n");
            for (int i = 0; i != argumentsArray.length; i++) {
                argumentsString.append(MarkdownUtils.getMultiLineMarkdownStringEntry("Arg " + (i + 1), argumentsArray[i], "-")).append("\n");
            }
        } else{
            argumentsString.append(" -");
        }

        return argumentsString.toString();
    }


    /**
     * Get a log friendly {@link String} for {@link List<String>} argumentsArray.
     * If argumentsArray are null or of size 0, then `Arguments: -` is returned. Otherwise
     * following format is returned:
     *
     * Arguments:
     * ```
     * Arg 1: `value`
     * Arg 2: 'value`
     * ```
     *
     * @param argumentsArray The {@link String[]} argumentsArray to convert.
     * @return Returns the log friendly {@link String}.
     */
    public static String getArgumentsLogString(String[] argumentsArray) {
        StringBuilder argumentsString = new StringBuilder("Arguments:");

        if (argumentsArray != null && argumentsArray.length != 0) {
            argumentsString.append("\n```\n");
            for (int i = 0; i != argumentsArray.length; i++) {
                argumentsString.append(getSingleLineLogStringEntry("Arg " + (i + 1), argumentsArray[i], "-")).append("`\n");
            }
            argumentsString.append("```");
        } else{
            argumentsString.append(" -");
        }

        return argumentsString.toString();
    }



    public static String getSingleLineLogStringEntry(String label, Object object, String def) {
        if (object != null)
            return label + ": `" + object + "`";
        else
            return  label + ": "  +  def;
    }

    public static String getMultiLineLogStringEntry( String label, Object object,String def) {
        if (object != null)
            return label + ":\n```\n" + object + "\n```\n";
        else
            return  label + ": "  +  def;
    }



    public boolean setState(ExecutionState newState) {
        // The state transition cannot go back or change if already at {@link ExecutionState#SUCCESS}
        if(newState.getValue() < currentState.getValue() || currentState == ExecutionState.SUCCESS) {
            Logger.logError("Invalid ExecutionCommand state transition from \"" + currentState.getName() + "\" to " +  "\"" + newState.getName() + "\"");
            return false;
        }

        // The {@link ExecutionState#FAILED} can be set again, like to add more errors, but we don't update
        // {@link #previousState} with the {@link #currentState} value if its at {@link ExecutionState#FAILED} to
        // preserve the last valid state
        if(currentState != ExecutionState.FAILED)
            previousState = currentState;

        currentState = newState;
        return  true;
    }

    public boolean setStateFailed(int errCode, String errmsg,  Throwable throwable) {
        if(errCode < 1)
            return false;

        if(!setState(ExecutionState.FAILED))
            return false;

        this.errCode = errCode;
        this.errmsg = errmsg;

        if(this.throwableList == null)
            this.throwableList =  new ArrayList<>();

        if(throwable != null)
            this.throwableList.add(throwable);

        return true;
    }

}
