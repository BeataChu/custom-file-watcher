This simple application is intended to track file modifications in a set of folders recursively. Any file modification in any of the folder will result in the same modification in other folders.

It is a command line application. In order to run it:
1) edit directories.json up to your needs
2) make sure you set java path as an environment variable:
   set path=path-to-jdk\bin
3) javac Main.java - compile the application
4) java Main path-to-watched-directory

javac Main.java
java Main