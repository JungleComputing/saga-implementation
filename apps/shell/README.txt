=================
SAGA shell README
=================

--------
Contents
--------

1. Introduction
2. General commands
   - help
   - exit
   - #
3. Context management commands
   - addc
   - lsc
   - rmc
4. File browsing commands
   - pwd
   - ls
   - cd
   - touch
   - mkdir
   - cp
   - mv
   - rm
   - cat
5. Job management commands
   - prm
   - run
   - jobs
   - kill

---------------
1. Introduction
---------------

The SAGA shell is an example Java SAGA application. It mimics a typical UNIX
shell, and allows a users to browse filesystems and run simple jobs. All 
commands in the shell use Java SAGA underneath.

The SAGA shell features:

- file system browsing (pwd, cd, ls, cp, mv, mkdir, rm, touch, cat)
- job management (run, jobs, kill)
- SAGA security context management
- command history
- tab completion

The prompt of the SAGA shell shows two URLs: the URL of the current resource 
manager and the URL of the current working directory. The current resource 
manager is used to execute jobs. The current working directory is the base of
relative paths used in all the commands.

Example prompt:
 
  [local://localhost+file://localhost/home/john]

In this example, the current resource manager is 'local://localhost' and the
current working directory is 'file://localhost/home/john'. 

-------------------
2. General commands
-------------------

'help'
  Prints a summary of all available commands.

'exit'
  Ends the SAGA shell. All background jobs that are still running are canceled.
  The default SAGA session is closed (to allow adaptors to cleanup).

'#'
  Comment character. Any line starting with '#' will be ignored by the
  SAGA shell. The comment character is very convenient in script files that
  are used as input to the SAGA shell.

------------------------------
3. Context management commands
------------------------------

'addc'
  Usage: addc <type> [key=val]
  
  Adds a security context of a certain type with the given attributes to the 
  default SAGA session. Without any parameters, the command lists all the
  recognized attributes. The first argument is always the context type.
  The attribute value 'ask' triggers a masked interactive prompt to enter the
  real value, which is especially useful for entered passwords.
  
  Examples:

    List all available attributes:
      $ addc

    Add an anonymous FTP context:
      $ addc ftp

    Add an SSH context with a username 'john' and a password. The password can
    be entered separately:
      $ addc ssh UserID=john UserPass=ask
      Value of 'UserPass': ********

'lsc'
  Shows a list of all security contexts. Each context is prefixed with a 
  zero-based number indicates its position in the list. For each context, 
  all attributes are shown that have a non-default value. The value of the 
  'UserPass' attribute is always shown as '<secret>'.
  
  Example:
  
    $ lsc
     [0] ftp UserID=anonymous UserPass=<secret>
     [1] ssh UserID=john UserPass=<secret>
  
'rmc'
  Usage: rmc <#context>
  
  Removes a security context from the list of contexts. The argument is the 
  context's position in the list, i.e. the number shown in the output of 'lsc'.
  
  Example:
  
    $ lsc
     [0] ftp UserID=anonymous UserPass=<secret>
     [1] ssh UserID=john UserPass=<secret>
    $ rmc 0
    $ lsc
     [0] ssh UserID=john UserPass=<secret>

-------------------------
4. File browsing commands
-------------------------

'pwd'
  Prints the current working directory.
  
'ls'
  Lists all entries in the current working directory. The entries are sorted
  alphabetically.
  
'cd'
  Usage: cd [url]

  Changes the current working directory. Without any arguments, the current
  working directory is set to the user's local home directory. Otherwise, the
  argument can be a relative path, an absolute path or a complete URL of a
  (possibly remote) filesystem. 
  
  All relative paths (in this command and other commands) are resolved against 
  the current working directory. Absolute paths are resolved within the same 
  filesystem as the current working directory.
  
  Examples: (showing only the current working directory in the prompt)
  
  Browse the local file system:
    [file://localhost/home/john/doc/tmp] cd ..
    [file://localhost/home/john/doc] cd /tmp
    [file://localhost/tmp] cd
    [file://localhost/home/john]
    
  Browse an anonymous FTP site:
    [file://localhost/home/john] addc ftp
    [file://localhost/home/john] cd ftp://ftp.xs4all.nl/
    [ftp://ftp.xs4all.nl] cd pub
    [ftp://ftp.xs4all.nl/pub] cd /
    [ftp://ftp.xs4all.nl] ls
    dev
    pub
    shlib
    welcome.msg
    
'touch'
  Usage: touch <url>
  
  Creates a new, empty file. The argument can be a relative path, an absolute
  path or a complete URL. 
  
  Examples:
  
  $ touch foo.txt
  $ touch /tmp/foo.txt
  $ touch ssh://host.example.com/tmp/foo.txt
    
'mkdir'
  Usage: mkdir <url>

  Creates a new directory. The argument can be a relative path, an absolute
  path or a complete URL. 
  
  Examples:
  
  $ mkdir newdir
  $ mkdir /tmp/newdir
  $ mkdir ssh://host.example.com/tmp/newdir
  
'cp'
  Usage:  cp <source> <target>
  
  Copies a file or directory. If the target is an already existing directory,
  the source is copied into that directory. Directories are copied recursively.
  Both source and target can be a relative path, an absolute path or a complete 
  URL.
  
  Examples:
  
  $ cp foo.txt bar.txt                     
  $ cp foo.txt /tmp/bar.txt                
  $ cp foo.txt sftp://host.example.com/tmp/bar.txt
  $ cp foo.txt /tmp
  $ cp ftp://ftp.xs4all.nl/welcome.msg file://localhost/tmp

'mv'
  Usage:  mv <source> <target>
  
  Moves a file or directory. If the target is an already existing directory,
  the source is moved into that directory. Both source and target can be a 
  relative path, an absolute path or a complete URL.
  
  Examples:
  
  $ mv foo.txt bar.txt                     
  $ mv foo.txt /tmp/bar.txt                
  $ mv foo.txt ssh://host.example.com/tmp/bar.txt
  $ mv foo.txt /tmp
  $ mv ssh://host.example.com/tmp/foo.txt file://localhost/tmp

'rm'
  Usage: rm <url>

  Removes a file or an empty directory. The argument can be a relative path, 
  an absolute path or a complete URL. For safety reasons, the SAGA shell 
  cannot remove non-empty directories.

'cat'
  Usage: cat <url>

  Shows the contexts of a file. The argument can be a relative path, an 
  absolute path or a complete URL.

--------------------------
5. Job management commands
--------------------------
  
'prm'
  Prints the URL of the current resource manager. The resource manager is used
  to execute jobs.
  
'crm'
  Usage: crm [url]

  Changes the current resource manager. Without any arguments, the current
  resource manager is set to local job execution (e.g. local://localhost). 
  Otherwise, the argument indicates the URL of a (possibly remote) resource
  manager. The Java SAGA User Guide lists the supported backends and the syntax 
  of their resource manager URLs.
  
  Examples:
  
  $ crm local://localhost         (for local job execution)
  $ crm ssh://host.example.com    (for execution on host.example.com via SSH)

'run'
  Usage: run [-out file] [-err file] <executable> [arg]* [&]
  
  Runs an executable using the current resource manager. The executable can be
  a relative or absolute path, which is evaluated by the current resource 
  manager. Note that this path is probably not related at all to the current
  working directory of the SAGA shell. The executable can have zero or more 
  arguments, separated by spaces.
  
  The executable may generate some stdout and/or stderr output. The SAGA shell 
  can handle this output in two ways:

  1. By default, the executable is run as an 'interactive' SAGA job. The stdout 
     and stderr streams of the job are then returned by SAGA. The SAGA shell 
     reads and prints those streams in two separate threads until the executable 
     has finished.

  2. When the -out and/or -err parameters have been specified, the stdout and 
     stderr output of the executable are redirected into files. The file names
     specified by these parameters can be relative or absolute paths, or 
     complete URLs. The SAGA shell will the parse the base names of each file 
     name and redirect the output of the executable into this file. When the 
     job is completed, this file is transferred to the specified files
     via 'file transfer' directives in the job description. Relative file names
     are resolved against the current working directory.
     
  By default, the executable is run in the foreground. The SAGA shell will then
  block until the executable has finished. When the run command ends with a 
  single '&', the command is run in the background and the SAGA shell does not
  block. Background jobs can be managed with the commands 'jobs' and 'kill'.
    
  Examples:
  
  Run '/bin/hostname' locally in the foreground:
    [local://localhost+file://localhost/] run /bin/hostname
    localhost

  Run '/bin/sleep 5' locally in the foreground:
    [local://localhost+file://localhost/] run /bin/sleep 5
    <blocks 5 seconds>
  
  Run '/bin/sleep 10' locally in the background:
    [local://localhost+file://localhost/] run /bin/sleep 10 &

  Run '/bin/hostname' on a remote machine via SSH:
    [local://localhost+file://localhost/] addc ssh UserID=john UserPass=ask
    Value of 'UserPass': ******
    [local://localhost+file://localhost/] crm ssh://host.example.com
    [ssh://host.example.com+file://localhost/] run /bin/hostname
    host.example.com
    
  Run '/bin/hostname' on a remote machine via SSH, and put stdout and stderr 
  in two files that are transferred back to localhost:
    [local://localhost+file://localhost/tmp] mkdir out
    [local://localhost+file://localhost/tmp/out] cd out
    [local://localhost+file://localhost/tmp/out] addc ssh UserID=john UserPass=ask
    Value of 'UserPass': ******
    [local://localhost+file://localhost/tmp/out] crm ssh://host.example.com
    [ssh://host.example.com+file://localhost/tmp/out] run -out h.out -err h.err /bin/hostname
    [ssh://host.example.com+file://localhost/tmp/out] ls
    h.out
    h.err
    [ssh://host.example.com+file://localhost/tmp/out] cat h.out
    host.example.com
    
'jobs'
   Shows a list of all background jobs (i.e. all 'run' commands that ended 
   with a '&'). The SAGA shell puts all background jobs in a SAGA task 
   container. For each background job, the following properties are shown:
   
   - cookie (the identifier of the job in the task container of background jobs)
   - state (RUNNING, CANCELED, FAILED, SUSPENDED or DONE)
   - SAGA object identifier
   - executable and arguments

   Jobs with a state FAILED, CANCELED or DONE are removed from the task 
   container of background jobs once their properties have been printed.
   Consequently, they will not be listed in subsequent 'jobs' commands.
   
   Example:
   
   [local://localhost+file://localhost/] run /bin/sleep 100 &
   [local://localhost+file://localhost/] run /bin/sleep 200 &
   [local://localhost+file://localhost/] jobs
   [0] RUNNING	[JavaGAT]-[0]	/bin/sleep 100
   [1] RUNNING	[JavaGAT]-[1]	/bin/sleep 200
   [local://localhost+file://localhost/]
   
'kill'
   Usage: kill <#job>
   
   Cancels a background job. The argument is the cookie of the job to cancel,
   i.e. its identifier in the task container of background jobs. The cookie of
   a job is shown in the first number on each line in the output of the 'jobs' 
   command.
   
   Example:
   
   [local://localhost+file://localhost/] run /bin/sleep 100 &
   [local://localhost+file://localhost/] run /bin/sleep 200 &
   [local://localhost+file://localhost/] jobs
   [0] RUNNING	[JavaGAT]-[0]	/bin/sleep 100
   [1] RUNNING	[JavaGAT]-[1]	/bin/sleep 200
   [local://localhost+file://localhost/] kill 0
   [local://localhost+file://localhost/] jobs
   [0] CANCELED	[JavaGAT]-[0]	/bin/sleep 100
   [1] RUNNING	[JavaGAT]-[1]	/bin/sleep 200

   