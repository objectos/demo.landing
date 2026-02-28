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

import objectos.way.Http;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PageTest {

  @DataProvider
  public Object[][] parseProvider() {
    return new Object[][] {
        {"N", AppView.HOME},
        {"M", AppView.MOVIE},
        {"S", AppView.SEATS},
        {"C", AppView.CONFIRM},
        {"T", AppView.TICKET},
        {"B", AppView.NOT_FOUND},
        {"x", AppView.NOT_FOUND},
    };
  }

  @Test(dataProvider = "parseProvider")
  public void parse(String q, AppView expected) {
    Http.Exchange http = Http.Exchange.create(opts -> {
      opts.queryParam("page", q);
    });

    AppView res = AppView.parse(http);

    assertEquals(res, expected);
  }

}