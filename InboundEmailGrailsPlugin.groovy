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

import org.subethamail.smtp.server.SMTPServer
import grails.util.Environment
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import grails.plugin.inboundemail.DefaultMessageListener
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import grails.plugin.inboundemail.PrintMessageHandler
import org.apache.james.mime4j.message.Message

class InboundEmailGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.4 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    // soft dependency on background-thread plugin, will block during receive without this plugin installed
    def loadAfter = ['background-thread']

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Dan Lynn"
    def authorEmail = "dan@danlynn.com"
    def title = "Run an embedded SMTP server in your grails app"
    def description = '''\\
Integrates SubEtha SMTP into your grails app, allowing you to plug in your own message handlers.
'''

    List watchedResources = [
		'file:./grails-app/services/**/*Service.groovy',
		'file:./plugins/*/grails-app/services/**/*Service.groovy',
		"file:./grails-app/conf/InboundEmailConfig.groovy"
	]

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-inbound-email"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        def config = getConfiguration().inboundEmail

        configureSmtpServer.delegate = delegate
        configureSmtpServer(config)

        discoverReceiveHandlerService.delegate = delegate
        discoverReceiveHandlerService(config)
    }

    /**
     * Attempts to find a Grails service class marked as follows:
     *
     * static exposes = ['smtp']
     *
     */
    private def discoverReceiveHandlerService = { config ->
        def exposedClasses = application.serviceClasses*.clazz.findAll { exposesSmtp(it) }
        
        if (exposedClasses.size() > 1) {
            println "More than 1 service class exposes smtp. The server will use ${exposedClasses.first()}"
            receiveHandlerService(exposedClasses.first())
        } else if (exposedClasses.size() == 0) {
            println "No service class exposes SMTP. The server will write messages to stdout."
            receiveHandlerService(PrintMessageHandler)
        } else {
            receiveHandlerService(exposedClasses.first())
        }
    }

    /**
     * Config override mechanism borrowed from Burt Beckwith's acegi plugin, now Spring Security Core. Thanks, Burt.
     * @return
     */
    static ConfigObject getConfiguration(Class configOverride=null) {
		GroovyClassLoader classLoader = new GroovyClassLoader(InboundEmailGrailsPlugin.classLoader)

		def slurper = new ConfigSlurper(Environment.current.name)
		ConfigObject userConfig
		try {
			userConfig = slurper.parse(configOverride ?: classLoader.loadClass('InboundEmailConfig'))
		}
		catch (e) {
			// ignored, use defaults
		}

		ConfigObject config
		ConfigObject defaultConfig = slurper.parse(classLoader.loadClass('DefaultInboundEmailConfig'))
		if (userConfig) {
			config = defaultConfig.merge(userConfig)
		}
		else {
			config = defaultConfig
		}

		return config
    }

    private def configureSmtpServer = { config ->
        inboundEmailMessageListener(DefaultMessageListener) {bean ->
            bean.autowire = 'byName'
        }

        inboundEmailMessageListenerAdaptor(SimpleMessageListenerAdapter, inboundEmailMessageListener)
        
        inboundEmailSmtpServer(SMTPServer, inboundEmailMessageListenerAdaptor) {
            port = config.port

            if (config.hostName) {
              hostName = config.hostName
            }
        }
    }

    def doWithDynamicMethods = { ctx ->

    }

    def doWithApplicationContext = { applicationContext ->
        def mime4jHelperService = applicationContext.getBean('mime4jHelperService')
        
        Message.metaClass.getText = {
            mime4jHelperService.extractTextBody(delegate)
        }
    }

    def onChange = { event ->
		if (event.source && event.ctx && event.application) {
			boolean isServiceClass = application.isServiceClass(event.source)
			boolean configChanged = 'InboundEmailConfig'.equals(event.source.name)
			if (configChanged || isServiceClass) {
                applicationContext.getBean('inboundSmtpServerService').stop()
                reconfigureSmtpServer applicationContext.getBean('inboundEmailSmtpServer'), getConfiguration(event.source).inboundEmail
                applicationContext.getBean('inboundSmtpServerService').start()
			}
		}
    }

    void reconfigureSmtpServer(inboundEmailSmtpServer, config) {
        inboundEmailSmtpServer.port = config.port
        if (config.hostName) {
          inboundEmailSmtpServer.hostName = config.hostName
        }
    }

    final static EXPOSES_SPECIFIER = "exposes"
    final static EXPOSE_SPECIFIER = "expose"
    final static EXPOSES_SMTP_SPECIFIER = "smtp"

    boolean exposesSmtp(service) {
        GrailsClassUtils.getStaticPropertyValue(service, EXPOSES_SPECIFIER)?.contains(EXPOSES_SMTP_SPECIFIER) || GrailsClassUtils.getStaticPropertyValue(service, EXPOSE_SPECIFIER)?.contains(EXPOSES_SMTP_SPECIFIER)
    }

    def onConfigChange = { event ->
    }
}
