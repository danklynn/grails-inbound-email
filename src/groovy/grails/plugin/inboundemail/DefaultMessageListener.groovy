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

import org.subethamail.smtp.helper.SimpleMessageListener
import org.apache.james.mime4j.message.Message

class DefaultMessageListener implements SimpleMessageListener {
    def backgroundService
    def receiveHandlerService

    boolean accept(String sender, String recipient) {
        receiveHandlerService.allowMessage(sender, recipient)
    }

    void deliver(String sender, String recipient, InputStream messageStream) {
        def message = new Message(messageStream)

        if (backgroundService) {
            backgroundService.execute("inbound email") {
                receiveHandlerService.onMessage sender, message
            }
        }
        else {
            receiveHandlerService.onMessage sender, message
        }
    }
}
