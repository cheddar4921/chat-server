# CHAT PROJECT FOR SCHOOL (*server-side*)

This is a chat project for school. This is the client side version.

## PREREQUISITES

To run this, you will need a version a java. We suggest using JRE Version 17. Here is some download guides for some operating systems:

### JAVA DOWNLOAD

- Ubuntu
    `# apt install openjdk-17-jdk`
- Mac
    `$ brew install openjdk@17`
- Windows
    On windows there is many different versions of the JDK package. Research which one better fits your needs.

### RUNNING

After having downloaded the latest version of the client and Java, you can execute this by command line using the following syntax:

`java -jar chat-server.jar <port>`

Note: If you insert a port value that isn't within the range of valid port values, it will default to 25575.

### ADDITIONAL PARAMETERS

you can also add `debug` at the end of your command line to run the program in debug mode, where it will log everything. 

Note: You need to insert `<port>` or else it won't work.

# CREDITS

Authors:

- Enrico Marinelli
- Lorenzo Bartolozzi

Libraries used:

- [Jackson](https://github.com/FasterXML/jackson)

# RELEASES

You can find our releases [here](https://github.com/cheddar4921/chat-server/releases/tag/Release).