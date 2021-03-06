package org.ogf.saga.apps.shell.command;

public interface Command {

    /**
     * Returns a concise description of the possible arguments of this command.
     * A command without any arguments must return an empty string. Long 
     * descriptions can be split over multiple lines by including '\n' 
     * characters.
     *   
     * @return a description of the possible arguments of this command.
     */
    public String getHelpArguments();

    /**
     * Returns a simple explanation of what this command does, preferably in
     * one sentence. Long explanations can be split over multiple lines by 
     * including '\n' characters.
     * 
     * @return an english explanation of this command.
     */
    public String getHelpExplanation();

    /**
     * Executes this command. Output should be written to System.out or 
     * System.err. The command may change the environment object. 
     *  
     * @param args an array of arguments provided on the command line. 
     *     The first element is always the command itself, the remaining 
     *     arguments are parameters.
     */
    public void execute(String[] args);

}
