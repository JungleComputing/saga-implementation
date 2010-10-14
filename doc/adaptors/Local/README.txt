Local adaptor README
--------------------

Introduction.
  The Local adaptor implements the SAGA packages 'namespace' and 'file' for the
  local filesystem. It is built on top of Java's local file API. Most namespace 
  operations use the class java.io.File underneath. Copying and accessing files 
  is done using the class java.nio.FileChannel. The FileInputStream adaptor and 
  the FileOutputStream adaptor use their java.io counterparts.
  
Using the Local adaptor.
  The adaptor recognizes the schemes 'local', 'file', and 'any'. It also accepts
  URLs without a scheme. Examples of recognized URLs:
  
  - file://localhost/etc/passwd
  - local://localhost/usr/bin
  - any://localhost/tmp
  - dir/foo.txt

  No specific contexts are required.
  
