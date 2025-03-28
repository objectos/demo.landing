/*
 * Copyright (C) 2024-2025 Objectos Software LTDA.
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

import demo.landing.AbstractTest;
import objectos.way.Http;
import org.testng.annotations.Test;

public class KinoTest extends AbstractTest {

  @Test
  public void testCase01() {
    final Http.TestingExchange http;
    http = Http.TestingExchange.create(config -> {
      config.method(Http.Method.GET);

      config.path("/index.html");

      config.queryParam("demo", "i-do-not-exist");
    });

    handle(http);

    assertEquals(http.responseStatus(), Http.Status.OK);

    assertEquals(
        writeResponseBody(http),

        """
        # Something Went Wrong

        """
    );
  }

  @Override
  protected final String testData() {
    return "";
  }

}