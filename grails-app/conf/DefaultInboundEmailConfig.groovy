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

inboundEmail {

    // The tcp port to listen on. Keep in mind that the JVM must be run as root in order to bind to port 25.
    port = 2525

    // the hostname reported by the SMTP server. Defaults to the current machine's hostname if the groovy truth is false
    hostName = null

}