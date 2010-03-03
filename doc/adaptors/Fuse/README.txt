===================
FUSE adaptor README
===================
 
Introduction
------------
The FUSE adaptor implements the SAGA packages 'namespace' and 'file'. 
The adaptor uses the FUSE to mount remote filesystems locally. See 
http://fuse.sourceforge.net/ for more details about FUSE. The locally mounted 
filesystems are then accessed again via the SAGA Local adaptor. All mounted 
filesystems are automatically unmounted when the JVM terminates.
  
Available filesystems are configured via properties in a saga.properties
file (see the documentation of org.ogf.saga.bootstrap.SagaProperties).
The default saga.properties file defines access to:
- SSH hosts via SSHFS (see http://fuse.sourceforge.net/sshfs.html)
- FTP servers via CurlFtpFs (see http://curlftpfs.sourceforge.net/)
- Samba shares via CIFS (see http://linux-cifs.samba.org/)
- XtreemFS (see http://www.xtreemfs.com)


Limitations  
-----------
Access to the mounted filesystems is done via the Local adaptor, and hence
the FUSE adaptor inherits all limitations of the Local adaptor.


Using the FUSE adaptor
----------------------

SSH hosts:
  - accepted schemes: ssh, fusessh, any
  - accepted context types: ssh
      The context attributes 'UserID' and 'UserPass' are used as the SSH user 
      and password. The context attribute 'UserKey' can be set to use a specific
      SSH key instead of the default ones.
  - example URLs:  
      ssh://example.com/tmp/foo.txt
      ssh://example.com:12345/dir/file.txt
  
FTP servers:
  - accepted schemes: ftp, fuseftp, any
  - accepted context types: ftp
      The context attributes 'UserID' and 'UserPass' are used as the FTP user 
      and password.
  - example URL:
      ftp://ftp.kernel.org/pub/
      fuseftp://ftp.xs4all.nl/welcome.msg

Samba shares:
  - accepted schemes: cifs, samba, any
  - accepted context types: cifs, smb
      The context attributes 'UserID' and 'UserPass' are used as the Samba user 
      and password.
  - example URLs:
      cifs://sharename@example.com/dir/file.txt
      smb://sharename@example.com/
      
XtreemFS volumes:
  - accepted schemes: xtreemfs, xtfs, any
  - accepted context types: xtreemos
      The context attributes 'UserKey', 'UserCert', and 'UserPass' can be used 
      to indicate the user's private key, certificate, and passphrase, 
      respectively, that should be used in the mount.
  - example URLs:
      xtreemfs://volume@example.com/dir/file.txt
      xtfs://volume@example.com/
    

Configuration
-------------
Each filesystem recognized by this adaptor is defined by six properties:
 
- schemes: a comma-separated list of accepted URL schemes
- contexts: a comma-separated list of recognized context types
- mount.point: the directory to use as the mount point of the remote filesystem
- mount.command: the command to mount a remote filesystem
- mount.input: the characters to use a standard input to the mount command
- umount.command: the command to unmount a remote filesystem

The command line elements of the mount.command and umount.command properties 
are separated by spaces. During initialization, the adaptor tries to execute
the mount and umount commands without any parameters. If one of the commands
cannot be found, the related filesystem is disabled.

The FUSE adaptor will try to mount all URLs with an accepted scheme that do not 
refer to local files. URLs without a scheme are also considered to be local.
The adaptor tries to mount a remote URL with each context of a recognized type. 
Finally, the adaptor also tries a mount without any context.
  
For each mount tried, the mount point, mount command, mount input and unmount
command are determined at runtime. These properties can contain variables 
that are substituted using the current URL and context. When the parsed mount 
command has been executed before, the related mount point is reused. URLs that 
refer to the same remote filesystem thereby reuse the same mount point.
 
Each variable in a property starts with a '%'. The available variables are:

  %context_usercert = UserCert in an accepted context
  %context_userid = UserID in an accepted context
  %context_userkey = UserKey in an accepted context
  %context_userpass = UserPass in an accepted context
  %java_filesep=<value of System property file.separator>
  %java_random=<value of java.util.Random.nextLong()>
  %java_tmpdir=<value of System property java.io.tmpdir>
  %java_userdir=<value of System property user.dir>
  %java_userhome=<value of System property user.home>
  %java_username=<value of System property user.name>
  %fs = filesystem name
  %mount_point = parsed value of the mount.point property of this filesystem
  %url_scheme = scheme of the URL
  %url_userinfo = user info in URL
  %url_host = host name (or address) in URL
  %url_port = port in URL
  %url_path = path in URL
  %url_fragment = fragment in URL

Each variable used in a property can or cannot be bound. A URL variable is 
bound if the URL used in the mount contains that specific part. A context 
variable is bound if the context used in the mount contains that specific 
attribute. Note that an empty string will NOT bind a variable. For example,
the default values of SAGA context attributes are empty strings and will
therefore not cause a %context_* variable to be bound.

Besides variables, the properties can contain special syntax:

- {text between curly brackets} means an part with a default value. Such a  part 
  must contain exactly one '|' character that splits the part in a left and a 
  right side. If all variables contained in the left side are bound, the left 
  side is included with the substituted variable values. If at least one of 
  these variables is not bound, the right part is included. Each part can 
  contain multiple variables. 
  
  Example: -p {%url_port|4444}
    if %url_port is bound to, e.g., '1234', add the part "-p 1234"
    if %url_port is not bound, add the part "-p 4444"

- [text between square brackets] means an optional part. It is a shorthand for
  the curly bracket syntax with an empty default value. 
  Example: [-p %url_port] is a shorthand for {-p %url_port|}.

- Variables that start with an extra '%' are, if bound, substituted by an empty 
  string instead of the bound value. When combined with curly brackets, one can 
  include arbitrary strings depending on whether a variable is bound or not.

  Example: {%%context_userpass-o AskPass|-o IgnorePass}
    if %context_userpass is set, add the part "-o AskPass"
    if %context_userpass is not set, add the part "-o IgnorePass"

When a parsed property still contains unbound variables, a BadParameter 
exception is thrown. Neither the curly nor the square brackets can be nested.

