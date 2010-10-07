package grails.inboundemail

import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import org.subethamail.smtp.server.SMTPServer
import org.subethamail.smtp.helper.SimpleMessageListener

class InboundSmtpServerService {
        SMTPServer inboundEmailSmtpServer

        boolean transactional = false

        boolean start() {
            try {
                inboundEmailSmtpServer.start()
                println "SMTP server started on port ${inboundEmailSmtpServer.port}"
                true
            }
            catch (Throwable ex) {
                if (ex.cause instanceof BindException && ex.cause.message == 'Permission denied') {
                    log.error "Failed to start SMTP server: could bind to port ${inboundEmailSmtpServer.port}. Only root has permission bind to port numbers under 1024"
                } else {
                    log.error "Failed to start SMTP server: ", ex
                }
                
                false
            }
        }

        /**
         * Stops the in-memory SMTP server
         */
        boolean stop() {
            if (inboundEmailSmtpServer && inboundEmailSmtpServer.isRunning()) {
                inboundEmailSmtpServer.stop()
                println "SMTP server stopped on port ${inboundEmailSmtpServer.port}"
                true
            } else {
                log.warn "Failed to stop SMTP server!"
                false
            }
        }
}
