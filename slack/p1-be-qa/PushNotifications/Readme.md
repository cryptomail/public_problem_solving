#TASK 2: PUSH NOTIFICATIONS  
Provide a high-level description of a system you would write to test Slack's push notification functionality. We would be able to use this system to test the following:
* Messages that should be pushed to a user's phone are properly detected and marked as "pending" in the database — that is, the message is pending a push to the user's mobile device
* Pending messages are pushed when the user goes offline or has been idle for 15 minutes
* Pending messages are pushed to all active push tokens that the user's devices have registered with us
* The messages sent to the push notification servers contain the correct content and are properly formatted
* Pushed messages are switched from the "pending" state to the "pushed" state in the DB
#High Level Description


###Requirements Details:
The mechanics of testing this will fall under the following capabilities requirements
####User state and stream activity management
1. We need a way to emit arbitrary messages into a user's stream
2. We need a way to aribtrarily set a user's idle state
3. We need a way to aribtrarily set a user's online state.
4. We need a way to read from the database for a particular user ID in order to assert whether they have pending messages.
5. We need a way to read from the database for a particular user ID in order to assert whether pending messages have been pushed

####Mock Push Server
1. We need a mock push service that real, production code can use via (IoC) configuration
2. This push server is interface conformant as a given, but it also has different control lines that are used by the test orchestration layer
3. This push server affords orchstration read control lines, so the the test orchestration layer can read what was written to the push network
4. This push server affords orchestration control lines to determine whether messages will be tagged as failure or success when attempting to write to the network.





