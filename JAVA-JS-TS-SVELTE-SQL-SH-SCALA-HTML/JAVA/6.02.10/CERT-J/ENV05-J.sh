#!/bin/bash
#Noncompliant Code Example (JVMTI)
#In this noncompliant code example, the JVMTI works by using agents that communicate #with the running JVM. These agents are usually loaded at JVM startup via one of the #command-line options -agentlib or -agentpath. In the following command, libname is #the name of the library to load while options are passed to the agent on startup.

${JDK_PATH}/bin/java -agentlib:libname=options ApplicationName
#Some JVMs allow agents to be started when the JVM is already running. This practice #is insecure in a production environment. Refer to the JVMTI documentation [JVMTI #2006] for platform-specific information on enabling/disabling this feature.
#
#Platforms that support environment variables allow agents to be specified in such #variables. "Platforms may disable this feature in cases where security is a
# concern; for example, the Reference Implementation disables this feature on UNIX #systems when the effective user or group ID differs from the real ID" [JVMTI 2006].
#
#Agents may run under the default security manager without requiring any permissions #to be granted. Although the JVMTI is useful for debuggers and profilers, such #levels of access are inappropriate for deployed production code.
#
#Noncompliant Code Example (JPDA)
#This noncompliant code example uses command-line arguments to invoke the JVM so #that it can be debugged from a running debugger application by listening for #connections using shared memory at transport address mysharedmemory:
${JDK_PATH}/bin/java -agentlib:jdwp=transport=dt_shmem,
    address=mysharedmemory ApplicationName
Likewise, the command-line arguments -Xrunjdwp (which is equivalent to -agentlib) and -Xdebug (which is used by the jdb tool) also enable application debugging.

#Noncompliant Code Example (JVM monitoring)
#This noncompliant code example invokes the JVM with command-line arguments that #permit remote monitoring via port 8000. This invocation may result in a security  #vulnerability when the password is weak or the SSL protocol is misapplied.
${JDK_PATH}/bin/java
    -Dcom.sun.management.jmxremote.port=8000 ApplicationName
	
#Noncompliant Code Example (Remote Debugging)
#Remote debugging requires the use of sockets as the transport #(transport=dt_socket). Remote debugging also requires specification of the type of #application (server=y, where y denotes that the JVM is the server and is waiting #for a debugger application to connect to it) and the port number to listen on #(address=9000).
${JDK_PATH}/bin/java -agentlib:jdwp=transport=dt_socket, 
    server=y,address=9000 ApplicationName
#Remote debugging is dangerous because an attacker can spoof the client IP address #and connect to the JPDA host. Depending on the attacker's position in the network,
# he or she could extract debugging information by sniffing the network traffic that #the JPDA host sends to the forged IP address.