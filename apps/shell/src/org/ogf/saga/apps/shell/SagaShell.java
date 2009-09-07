package org.ogf.saga.apps.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.ogf.saga.apps.shell.command.*;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jline.ArgumentCompletor;
import jline.CandidateListCompletionHandler;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

public class SagaShell {

    private static final String COMMENT = "#";
    
    private Logger logger = LoggerFactory.getLogger(SagaShell.class);
    
    protected Environment env;
    protected ConsoleReader console;
    protected SagaFileNameCompletor fileNameCompletor;
    protected Map<String, Command> commands;
    
    public SagaShell() throws Exception {
    	this(new Environment());
    }
    
    public SagaShell(Environment env) throws Exception {
    	this.env = env;
    	
        // create a console 
        console = new ConsoleReader();
        
        // create a list of possible commands
        commands = new LinkedHashMap<String, Command>();
        commands.put("help", new Help(commands));
        commands.put("exit", new Exit(env));
        commands.put("addc", new AddContext(console));
        commands.put("lsc", new ListContexts());
        commands.put("rmc", new RemoveContext());
        commands.put("pwd", new PrintWorkingDirectory(env));
        commands.put("ls", new ListDirectory(env));
        commands.put("cd", new ChangeWorkingDirectory(env));
        commands.put("cp", new CopyEntry(env));
        commands.put("mv", new MoveEntry(env));
        commands.put("mkdir", new MakeDirectory(env));
        commands.put("rm", new RemoveEntry(env));
        commands.put("touch", new CreateFile(env));
        commands.put("cat", new PrintFile(env));
        commands.put("prm", new PrintResourceManager(env));
        commands.put("crm", new ChangeResourceManager(env));
        commands.put("run", new RunJob(env));
        commands.put("jobs", new ListJobs(env));
        commands.put("kill", new KillJob(env));
    }

    protected void init() {
        // initialize the console and tab completion
        CandidateListCompletionHandler h = new CandidateListCompletionHandler();
        h.setAlwaysIncludeNewline(false);
        console.setCompletionHandler(h);
        
        String[] commandArr = new String[commands.size()];
        commands.keySet().toArray(commandArr);
        
        // create the file name completor
        fileNameCompletor = new SagaFileNameCompletor();
        Completor[] subCompletors = new Completor[2];
        subCompletors[0] = new SimpleCompletor(commandArr);
        subCompletors[1] = fileNameCompletor;
        ArgumentCompletor completor = new ArgumentCompletor(subCompletors);
        completor.setStrict(false);
        console.addCompletor(completor);
    }
    
    public void run() {
        logger.debug("Initializing console...");
    	init();
    	
    	System.out.println("Type 'help' for help");
        
        while(!env.isTerminated()) {
            try {
                String[] tokens = readCommand();
                
                if (tokens != null && tokens.length > 0) {
                    Command c = commands.get(tokens[0]);
                    if (c != null) {
                        c.execute(tokens);
                        updateFileNameCompletor();
                    } else {
                        System.err.println("Unknown command: " + tokens[0]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
                env.terminate();
            }
        }
    }

    protected String getPrompt() {
        try {
            URL rm = env.getResourceManager();
            URL cwd = env.getCwd().getURL();
            return "[" + rm + "+" + cwd + "] ";
        } catch (SagaException e) {
        	logger.warn("Cannot create the prompt", e);
            return "[?+?] ";
        }
    }

    private String[] readCommand() throws IOException {
        String prompt = getPrompt();
        String line = console.readLine(prompt);
        
        if (line == null) {
        	// user pressed CTRL-D
        	System.out.println();
        	env.terminate();
        	return null;
        } else if (line.startsWith(COMMENT)) {
            // ignore comment line
            return null;
        } else {
        	// tokenize input
            StringTokenizer tokenizer = new StringTokenizer(line);
            
            ArrayList<String> tokens = new ArrayList<String>(tokenizer.countTokens());
    
            while (tokenizer.hasMoreTokens()) {
                tokens.add(tokenizer.nextToken());
            }
    
            return tokens.toArray(new String[0]);
        }
    }

    private void updateFileNameCompletor() {
    	try {
            URL cwd = env.getCwd().getURL();
            fileNameCompletor.setBase(cwd);
    	} catch (SagaException e) {
    		logger.warn("Cannot update file name completor", e);
    	}
    }
    
    public static void main(String[] argv) {
        System.out.println("Starting the SAGA shell...");

    	SagaShell shell;
    	try {
            shell = new SagaShell();
        } catch (Exception e) {
            System.err.println("Error while starting the SAGA shell:");
            e.printStackTrace(System.err);
            return;
        }
        
        shell.run();
    }
    
}
