package org.ogf.saga.apps.shell.command;

import java.util.Map;

public class Help implements Command {

    private static final String INDENT = " ";
    private static final int LEFT_COL_WIDTH = 16; // spaces

    private Map<String, Command> commands;

    public Help(Map<String, Command> commands) {
        this.commands = commands;
    }

    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "print this help text";
    }
    
    private void printIndent(int len) {
        for (; len > 0; --len) {
            System.out.print(' ');
        }
    }
    
    /**
     * For commands with multiple lines of arguments, try to print them nicely,
     * with indentation such that all the lines have the same start column.
     */
    private void printHelpArguments(String name, String helpArguments) {
        System.out.print(INDENT + name + INDENT);
        int cmdArgsIndent = name.length() + 2;
        
        String[] lines = helpArguments.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            if (i > 0) {
                System.out.println();
                printIndent(cmdArgsIndent);
            }
            System.out.print(lines[i]);
        }
    }

    public void execute(String[] args) {
        System.out.println("Available commands:");

        for (Map.Entry<String, Command> c : commands.entrySet()) {
            String name = c.getKey();
            Command cmd = c.getValue();
            String helpArguments = cmd.getHelpArguments();

            printHelpArguments(name, helpArguments);
            int cmdArgLen = name.length() + helpArguments.length() + 2;
            // If the command arguments help is only one line and we still have
            // room, continue with command explanation on same line. 
            if (cmdArgLen >= LEFT_COL_WIDTH ||
                    cmd.getHelpArguments().contains("\n")) {
                System.out.println();
                cmdArgLen = 0;
            } else {
                System.out.print(INDENT);
                ++cmdArgLen;
            }

            // print explanation; indent each new line
            String explanation = cmd.getHelpExplanation();
            String[] lines = explanation.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    printIndent(LEFT_COL_WIDTH - cmdArgLen);
                } else {                    
                    printIndent(LEFT_COL_WIDTH);
                }
                System.out.println(lines[i]);
            }
        }
    }

}
