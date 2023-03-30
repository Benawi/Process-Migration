# Process-Migration
## Distributed Systems- Code Migration Demo

## DESIGN
This distributed system architecture has the following components:
	1. Master node
	2. Multiple Slave nodes
All the components communicate with each other as follows:
● The slave nodes continuously listening on particular predefined ports.
● The master connects to the various slaves via sockets to issue the required commands.

The master node waits on the user to enter a particular command. The framework supports the
following commands:

## 1. LAUNCH
Used to launch a new process. The syntax is:
launch <process_name> <arguments>
This command makes the master choose one of the slave nodes in a round robin
fashion and fork the process <process_name> with the required process arguments
<arguments>.
On the successful execution of the command, the process gets started on the
corresponding slave node and the master outputs P_name. P_name is the name of the
job that just got created. The string looks like this:
	Pname: <slave>_<process_name>_<identifier>
This basically has 3 parts to it. They are:
	a) <slave>: This is the name of the slave node on which the process is running on.
	b) <process_name>: This is the process that is running on the slave node. It is same as
		the process name mentioned to launch the process above.
	c) <identifier>: This is the unique identifier that is used to distinguish this process on this
slave from other similar named processes on the same node.
This P_name will be used for identifying the process while migrating the process.

## 2. MIGRATE
This command is used to migrate a process from one slave node to another. The syntax
is as follows:
migrate <P_name> <destination_slave>
The user can choose any running process P_name and choose to migrate it to any slave
node he wishes to using this command.
Once the command is successfully executed, it returns the new P_name coherent to the
new slave node its running on.

## 3. KILL
This command is used to kill any running process. The syntax is as follows:
kill <P_name>
The user can choose to kill any running process using this command. Once the
command is successfully executed, it prints a message saying what got killed.

## 4. PEEK
This command is used to peek at the current running process list. The syntax is:
peek
It does not have any arguments. It prints out a list of all the P_names. This information
can be used to determine which process to migrate or kill.
When this command is called, the master node pings each slave with the various
P_names to determine if they’re still running of if they exited (with an error or gracefully). It
then populates the list and prints it to the user.
Each node (master and different slaves) have their own instances running on different nodes.
The slaves are listening on a particular port and the command line prompt is available to the user
on the master node for input. Once the user enters one of the commands viz. launch, remove,
migrate and peek, the master initiates a connection with one of the slaves (in round-robin
style)
for the launch command or a particular slave in case of a migrate or kill command and sends out
the requested command to it. Once the process is started on the slave, the slave returns the
required output to the master which is printed on the terminal. The call made by the user is
blocked until this happens.
Along with the handin, there are two other test files apart from Grepprocess.java. THey can be
used to test the code with.


HOW TO BUILD AND RUN IT
1) We have assumed that there are 2 slave nodes and 1 master node. If you wish to
use more slave nodes, please create a copy of srcSlave1 or srcSlave2 and edit some
hardcoded details (like the hostname/port of the machine) in the main file.
2) The srcMaster folder must be used in the master node machine and srcSlave* must be used
in the slave node machines. For our test, we used cs.wku.edu.
3) In the srcMaster folder, there is a file called MasterMain.java in the framework folder. Open it
up and customize the following line to the machine’s hostname addresses:
	Slave s1 = new Slave("SlaveOne", "cs1.wku.edu.", 6000);
	Slave s2 = new Slave("SlaveTwo", "cs2.wku.edu.", 6001);
Here, machine names like “cs1.wku.edu.” can be changed to the machine name the
code is running on. Do this change for SlaveMain.java file for the other folders too.
4) To compile each folder, just outside the srcMaster directory, enter:
	javac srcMaster/framework/*.java srcMaster/process/*.java
5) Perform the above step for the other folders too, eg.:
	javac srcSlave1/framework/*.java srcSlave1/process/*.java
	javac srcSlave2/framework/*.java srcSlave2/process/*.java
6) Once the java code is compiled, for each of the nodes, enter their src folder, i.e. srcMaster,
srcSlave1, srcSlave2 and enter the following for each:
	java cp
	. framework.MasterMain for
	srcMaster Master
	node
	java cp
	. framework.SlaveMain for
	srcSlave1 and srcSlave2 Slave
	nodes
7) This should start the program and there will be a command prompt on the Master node. Use
the commands like launch, migrate, kill and peek to perform operations.


## Example run log:

## Steps
![Migration](https://user-images.githubusercontent.com/21217148/225671473-b8e8cb62-2b91-4425-8d88-e771dc6ab105.JPG)
