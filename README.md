# Tickster
A Java based AI agent that accepts queries from a user and based on the answers for various tags, it uses machine learning to decide which team should the ticket be sent to.

Compiling and running:
- The code uses encog library so the file needs to be included in the classpath while compiling and running.
- The code can be compiled using:
-javac <classpath> A4main.java
- and can run with the following:
-java <classpath> A4main [any parameters]
- EX: -java -cp “classpath” A4main Adv
- Then just just the series of questions asked by the agent to get specific responses.
  
Design:
- PEAS:
  - Performance measure – Accuracy of the routing prediction and how early the
prediction was.
  - Environment – An online ticket booking system
  - Actuators – questions and predictions
  - Sensors – user input
- Problem definition: To design an agent to train on a dataset to be able to then
handle requests from the user and send it to the correct team that should handle
the ticket.
- System Architecture: The system has four classes:
  - The main class handles the arguments provided to decide which agent to
implement. According to the implemented agent, it calls the relevant class
while taking some input from the user if necessary.
  - The Basic class creates the network (or takes it from input if provided on
object creation) and trains it either according to the dataset provided or
with the relevant inputs/outputs is provided during object creation. If the
save function is called, the network is saved and retained for future use.
  - The IOData class is reading the data from the dataset and breaking it down
into headings and data then further breaking it into output and input data.
Then it encodes the data (the input is a direct 1 for yes and a 0 for no,
whereas binary encoding is used for the outputs). This class can also
restructure the data if new teams or tags are added by an admin (as per the
advanced agent requirements).
  - The Intermediate class loads the network (or gets it during object creation)
and then uses that network to get an output for the user provided input. It
looks through the dataset starting after the 4 th user input to find similar
information to try and fill the inputs, if it cant it assumes the other
answers to be a no and runs the input through the network to get an output.
The user can then choose if the output is correct or not and if its not the
other questions are asked, otherwise the user is given the option to book a
new ticket. If the answer even after the last question is wrong, the user is
prompted to choose the correct team from a list and then the network retrains
itself to the correct output as defined by the user.
