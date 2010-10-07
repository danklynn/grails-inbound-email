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

package grails.inboundemail

import grails.test.GrailsUnitTestCase
import org.apache.james.mime4j.message.Message
import org.junit.Test


class Mime4jHelperMethodsTest extends GrailsUnitTestCase {
    @Test void test_Message_text_property() {
        def expectedText = "this is some text!"
        Message.metaClass.getText = {
            expectedText
        }

        assert new Message().text == expectedText 
    }
}
