/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
//START SNIPPET: code
package org.superbiz.mdb;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagingClientBean implements MessagingClientLocal {
    public void sendMessage(String text) throws JMSException {

        Connection connection = null;
        Session session = null;

        try {
            InitialContext context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:comp/env/ConnectionFactory");

            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a MessageProducer from the Session to the Topic or Queue
            Queue questionQueue = (Queue) context.lookup("java:comp/env/ChatBean");
            MessageProducer producer = session.createProducer(questionQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a message
            TextMessage message = session.createTextMessage(text);

            // Tell the producer to send the message
            producer.send(message);
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            // Clean up
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }

    public String receiveMessage() throws JMSException, NamingException {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            InitialContext context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:comp/env/ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a MessageConsumer from the Session to the Topic or Queue
            Queue answerQueue = (Queue) context.lookup("java:comp/env/AnswerQueue");
            consumer = session.createConsumer(answerQueue);

            // Wait for a message
            TextMessage message = (TextMessage) consumer.receive(1000);

            return message.getText();
        } finally {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        }

    }
}
//END SNIPPET: code
