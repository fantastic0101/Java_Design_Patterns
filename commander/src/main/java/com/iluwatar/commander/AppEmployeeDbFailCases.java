/*
 * The MIT License
 * Copyright © 2014-2019 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.iluwatar.commander;

import com.iluwatar.commander.employeehandle.EmployeeDatabase;
import com.iluwatar.commander.employeehandle.EmployeeHandle;
import com.iluwatar.commander.exceptions.DatabaseUnavailableException;
import com.iluwatar.commander.exceptions.ItemUnavailableException;
import com.iluwatar.commander.messagingservice.MessagingDatabase;
import com.iluwatar.commander.messagingservice.MessagingService;
import com.iluwatar.commander.paymentservice.PaymentDatabase;
import com.iluwatar.commander.paymentservice.PaymentService;
import com.iluwatar.commander.queue.QueueDatabase;
import com.iluwatar.commander.shippingservice.ShippingDatabase;
import com.iluwatar.commander.shippingservice.ShippingService;

/**
 * AppEmployeeDbFailCases class looks at possible cases when Employee handle service is
 * available/unavailable.
 */
public class AppEmployeeDbFailCases {
  final int numOfRetries = 3;
  final long retryDuration = 30000;
  final long queueTime = 240000; //4 mins
  final long queueTaskTime = 60000; //1 min
  final long paymentTime = 120000; //2 mins
  final long messageTime = 150000; //2.5 mins
  final long employeeTime = 240000; //4 mins

  void employeeDatabaseUnavailableCase() throws Exception {
    PaymentService ps =
        new PaymentService(new PaymentDatabase(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException());
    ShippingService ss = new ShippingService(new ShippingDatabase());
    MessagingService ms = new MessagingService(new MessagingDatabase());
    EmployeeHandle eh =
        new EmployeeHandle(new EmployeeDatabase(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException());
    QueueDatabase qdb =
        new QueueDatabase(new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException(), new DatabaseUnavailableException());
    Commander c = new Commander(eh, ps, ss, ms, qdb, numOfRetries, retryDuration,
        queueTime, queueTaskTime, paymentTime, messageTime, employeeTime);
    User user = new User("Jim", "ABCD");
    Order order = new Order(user, "book", 10f);
    c.placeOrder(order);
  }

  void employeeDbSuccessCase() throws Exception {
    PaymentService ps = new PaymentService(new PaymentDatabase());
    ShippingService ss =
        new ShippingService(new ShippingDatabase(), new ItemUnavailableException());
    MessagingService ms = new MessagingService(new MessagingDatabase());
    EmployeeHandle eh =
        new EmployeeHandle(new EmployeeDatabase(), new DatabaseUnavailableException(),
            new DatabaseUnavailableException());
    QueueDatabase qdb = new QueueDatabase();
    Commander c = new Commander(eh, ps, ss, ms, qdb, numOfRetries, retryDuration,
        queueTime, queueTaskTime, paymentTime, messageTime, employeeTime);
    User user = new User("Jim", "ABCD");
    Order order = new Order(user, "book", 10f);
    c.placeOrder(order);
  }

  /**
   * Program entry point.
   *
   * @param args command line args
   */

  public static void main(String[] args) throws Exception {
    AppEmployeeDbFailCases aefc = new AppEmployeeDbFailCases();
    //aefc.employeeDatabaseUnavailableCase();
    aefc.employeeDbSuccessCase();
  }
}
