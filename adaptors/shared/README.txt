Why the "shared directory" ?

Some instances need to be moved from one adaptor to another. This happens, for
example, when any globus functionality is used from another adaptor.
Also, there are issues when security objects are loaded by a classloader
that is specific for a certain adaptor. For some unclear (to me) reason,
this issue arises when for a job submission, first the gridsam adaptor
and then the javagat adaptor is tried.

Instances can only be moved from one adaptor to another adaptor if they are 
loaded in the same or a common parent classloader. The current classloader
model is therefore:

superparent --- shared +-- adaptor1
                       +-- adaptor2
                       +-- adaptor3

where:
  superparent 
    loads the Saga engine and the application. 
  shared 
    loads all classes where instances may need to be moved between adaptors
  adaptorX 
    is the classloader for the particular adaptor. It is important that this 
    classloader does NOT load any classes where instances are passed to another
    adaptor.
