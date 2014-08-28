package com.milaboratory.mitools.com.milaboratory.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class JCommanderBasedMain implements ActionHelper {
    // LinkedHashMap to preserve order of actions
    protected final Map<String, Action> actions = new LinkedHashMap<>();
    protected final String command;
    protected boolean printHelpOnError = false;
    protected PrintStream outputStream = System.err;

    public JCommanderBasedMain(String command, Action... actions) {
        this.command = command;
        for (Action action : actions)
            reg(action);
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public PrintStream getDefaultPrintStream() {
        return outputStream;
    }

    protected void reg(Action a) {
        actions.put(a.command(), a);
    }

    public void main(String... args) throws Exception {
        // Setting up JCommander
        MainParameters mainParameters = new MainParameters();
        JCommander commander = new JCommander(mainParameters);
        commander.setProgramName(command);
        for (Action a : actions.values())
            commander.addCommand(a.command(), a.params());

        // Getting command name
        String commandName = args[0];

        // Getting corresponding action
        Action action = actions.get(commandName);

        try {
            if (action != null && (action instanceof ActionParametersParser)) {
                ((ActionParametersParser) action).parseParameters(Arrays.copyOfRange(args, 1, args.length));
            } else {
                commander.parse(args);

                // Print complete help if requested
                if (mainParameters.help()) {
                    StringBuilder builder = new StringBuilder();
                    commander.usage(builder);
                    outputStream.print(builder);
                    return;
                }

                // Getting parsed command
                // assert parsedCommand.equals(commandName)
                final String parsedCommand = commander.getParsedCommand();

                // Processing potential errors
                if (parsedCommand == null || !actions.containsKey(parsedCommand)) {
                    if (parsedCommand == null)
                        outputStream.println("No command specified.");
                    else
                        outputStream.println("Command " + parsedCommand + " not supported.");
                    outputStream.println("Use -h option to get a list of supported commands.");
                    return;
                }

                action = actions.get(parsedCommand);
            }

            if (action.params().help()) {
                printActionHelp(commander, action);
            } else {
                action.params().validate();
                action.go(this);
            }
        } catch (ParameterException pe) {
            printException(pe, commander, action);
        }
    }

    protected void printActionHelp(JCommander commander, Action action) {
        StringBuilder builder = new StringBuilder();
        if (action instanceof ActionHelpProvider)
            ((ActionHelpProvider) action).printHelp(builder);
        else
            commander.usage(action.command(), builder);
        outputStream.print(builder);
    }

    protected void printException(ParameterException e,
                                  JCommander commander, Action action) {
        outputStream.println("Error: " + e.getMessage());
        printActionHelp(commander, action);
    }

    public static final class MainParameters {
        @Parameter(names = {"-h", "--help"}, help = true, description = "Displays this help message.")
        public Boolean help;

        public boolean help() {
            return help != null && help;
        }
    }
}