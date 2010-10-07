A Grails plugin that integrates [http://code.google.com/p/subethasmtp/](SubEtha SMTP) into your grails app, allowing you to plug in your own message handlers.

#### Example usage

    class MySmtpService {
        static expose = ['smtp']

        // allows you to reject unknown recipients before the SMTP client is allowed to continue.
        def allowMessage(String sender, String recipient) {
            true
        }

        // processes the message. If you have the background-thread plugin installed, this occurs in a background thread.
        def onMessage(String sender, Message message) {
            println "Message from ${sender}, textBody: ${message.text}"
        }
    }

#### Current Status

Finished a spike. No tests to speak of yet.

#### TODOs

* Unit test all classes
* Integration test using local tcp, perhaps with an optional grails-mail plugin installed to make it cleaner. Will
  need to write a special message handler that exposes callbacks to the test.

#### Contributors

* Dan Lynn - [http://github.com/danklynn/](http://github.com/danklynn/)




