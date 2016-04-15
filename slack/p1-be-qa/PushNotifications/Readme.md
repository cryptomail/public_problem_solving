TASK 2: PUSH NOTIFICATIONS
Provide a high-level description of a system you would write to test Slack's push notification functionality. We would be able to use this system to test the following:
Messages that should be pushed to a user's phone are properly detected and marked as "pending" in the database — that is, the message is pending a push to the user's mobile device
Pending messages are pushed when the user goes offline or has been idle for 15 minutes
Pending messages are pushed to all active push tokens that the user's devices have registered with us
The messages sent to the push notification servers contain the correct content and are properly formatted
Pushed messages are switched from the "pending" state to the "pushed" state in the DB