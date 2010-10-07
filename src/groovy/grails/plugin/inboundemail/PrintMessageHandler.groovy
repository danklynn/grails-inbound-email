/* Copyright 2010 Dan Lynn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugin.inboundemail

import org.apache.james.mime4j.message.Message

/**
 * A default class to print messages to stdout if the application has not marked a service class as "static expose = ['smtp']"
 */
class PrintMessageHandler {

    boolean allowMessage(String sender, String recipient) {
        true
    }

    def onMessage(String sender, Message message) {
        def cc = message.cc?.collect{it.displayString}?.join(', ')
        def bcc = message.cc?.collect{it.displayString}?.join(', ')

        println """
Email received:
-----------------------
From: ${sender}
To: ${message.to?.collect{it.displayString}?.join(', ')}
"""

    if (cc)
        println "Cc: ${cc}"

    if (bcc)
        println "Bcc: ${bcc}"

    println """Subject: ${message.subject}
Body:
${message.text}

"""
    }
}
