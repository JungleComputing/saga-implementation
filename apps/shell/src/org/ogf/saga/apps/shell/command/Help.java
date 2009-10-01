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

    public void execute(String[] args) {
        System.out.println("Available commands:");

        for (Map.Entry<String, Command> c : commands.entrySet()) {
            String name = c.getKey();
            Command cmd = c.getValue();

            System.out.print(INDENT);

            String cmdArg = name + " " + cmd.getHelpArguments();
            System.out.print(cmdArg);

            int cmdArgLen = cmdArg.length();
            if (cmdArgLen > LEFT_COL_WIDTH) {
                System.out.println();
                System.out.print(INDENT);
                cmdArgLen = 0;
            }

            for (int i = 0; i < LEFT_COL_WIDTH - cmdArgLen; i++) {
                System.out.print(' ');
            }

            System.out.println(cmd.getHelpExplanation());
        }
    }

}
