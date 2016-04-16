#TASK 2: PUSH NOTIFICATIONS  
Provide a high-level description of a system you would write to test Slack's push notification functionality. We would be able to use this system to test the following:
* Messages that should be pushed to a user's phone are properly detected and marked as "pending" in the database — that is, the message is pending a push to the user's mobile device
* Pending messages are pushed when the user goes offline or has been idle for 15 minutes
* Pending messages are pushed to all active push tokens that the user's devices have registered with us
* The messages sent to the push notification servers contain the correct content and are properly formatted
* Pushed messages are switched from the "pending" state to the "pushed" state in the DB

#High Level Description

##Grand Assumptions And Necessities:
1. The Slack back end consists of a series of broken out services to perform atomic operations on their specific domains
2. The Slack back end is composable via configuration, and interfaces may be easily configured through fluent IoC or config files, thus allowing conformant interfaces to be switched out relatively easily for the purposes of testing.


###Requirements Details:
The mechanics of testing this will have the following capabilities requirements
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

####Orchestration Layer For Testing
1. The testing orchestration agent will write to the user service and set the user idle state, and user stream activity manager module
2. The orchestration agent will read from the (specially constructed) orchestration lines from the mock push server
3. The orchestration agent will write to the (specially constructed) orchestration lines from the mock push server
4. The orchestration agent will be able to read arbitrary data from the push db.


###Testing the system
####Setup:
1. Put in place a replacement IPushServer that records all traffic emitted out and allows the test orchestrator to determine whether the push should fail/succeed.
2. Enusre the test orchestrator can inject messages into the system with an internal interface, and also talk to the composed UserService which determines idle status.  I don't know if things are composed this way but that's how I'd architect it.
3. Ensure the test orchestrator can talk to the test IPushServer and set whether the push will succeed/fail with setAuthFail(true||false)

####Testing
1. Ensure the user is idle or not idle.  For push notifications set the user to idle simulating 15min of idle
2. Insert messages into the stream by talking to IMessageServer
3. Assertion: Ensure the slack message database has a 'pending' status for the uniquey identifiable message injected into the system if the user is tagged as idle or offline.
4. Assertion: Wait for the slack push processor to pick up the message that is 'pending' and sets the message state in the database to 'pending-processing' 
5. Assertion: Read all push messages sent out for a user by querying the *TEST* version of the push server with the special Test interface, and ensure that we can read the expected pushes that were supposed to go out based on the number of devices registered, available via the UserService.  Assert correct cardinality, and device identifiers and formatting of output formatting.
6. Assertion: Read the push database directly with the credentials given in configuration to assert the data records for those messages have been set to 'pushed' || retry based on configured fail state of mock push server.




![alt text](https://raw.githubusercontent.com/cryptomail/public_problem_solving/master/slack/p1-be-qa/PushNotifications/images/SlackPush.png "Slack Push Notifications Testing")




