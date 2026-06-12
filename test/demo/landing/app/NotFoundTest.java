/*
 * Copyright (C) 2024-2026 Objectos Software LTDA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.landing.app;

import static org.testng.Assert.assertEquals;

import objectos.http.HttpExchange;
import objectos.http.RequestMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class NotFoundTest {

  @Test
  public void testCase01() {
    final HttpExchange http;
    http = Testing.http(config -> {
      config.method(RequestMethod.GET);

      config.path("/demo.landing/not-found");

      config.testable();
    });

    assertEquals(
        Testing.handle0(http),

        """
        HTTP/1.1 404 Not Found\r
        Date: Mon, 28 Apr 2025 13:01:00 GMT\r
        Content-Type: text/html; charset=utf-8\r
        Transfer-Encoding: chunked\r
        \r
        # Something Went Wrong

        """
    );
  }

}
