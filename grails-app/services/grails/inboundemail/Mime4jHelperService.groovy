package grails.inboundemail

import org.apache.james.mime4j.message.TextBody
import org.apache.james.mime4j.message.Message

/**
 * Provides helper methods for dealing with {@link Message}s
 */
class Mime4jHelperService {
    static exposes = ['smtp']

    static transactional = false

    def extractTextBody(Message message) {
        ((TextBody)message.body).reader.text
    }
}
