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

import demo.landing.LandingDemo;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import objectos.way.Http;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testing.class)
public class AppCtxTest {

  private AppCtx ctx;

  @BeforeClass
  public void beforeClass() {
    final LandingDemo demo;
    demo = Testing.INJECTOR.getInstance(LandingDemo.class);

    ctx = (AppCtx) demo;
  }

  // ##################################################################
  // # BEGIN: History/Hash
  // ##################################################################

  @DataProvider
  public Object[][] decodeHashProvider() {
    return new Object[][] {
        // no value
        {null, null, "Request without Demo-Location-Hash header value"},
        {"", null, "Request without hash"},

        // valid
        {"#demo=9d8c39141d3c5e7b9a0f2d4c6e8b1a3f5c;", "/demo.landing/home", "valid"},

        // invalid
        {"demo=foo;", null, "no initial hash"},
        {"#demox=foo;", null, "name should be 4 chars long"},
        {"#sort=foo;", null, "name should be 'demo'"},
        {"#demo=3c8c90958b1a3f5c7d9e0b7b9e2a4f6c8d", null, "no trailing semicolon"},
        {"#demo=3c8c90958b1a3f5c7d9e0b7b9e2a4f6cd;", null, "hash has incorrent length"}
    };
  }

  @Test(dataProvider = "decodeHashProvider")
  public void decodeHash(String headerValue, String expected, String description) {
    final String result;
    result = ctx.decodeHash(headerValue);

    assertEquals(result, expected, description);
  }

  @DataProvider
  public Object[][] encodeHashProvider() {
    return new Object[][] {
        {AppView.HOME, 0, 0L, "9d8c39141d3c5e7b9a0f2d4c6e8b1a3f5c"},
        {AppView.CONFIRM, 0, 40306685673624018L, "9d8c39141e3c5e7b9a0fa27ed91abc6e8e"},
        {AppView.SEATS, 1031, 10902L, "9d8c39141f3c5e7f9d0f2d4c6e8b1a15ca"}
    };
  }

  @Test(dataProvider = "encodeHashProvider")
  public void encodeHash(AppView view, int id, long reservationId, String expected) {
    final String result;
    result = ctx.encodeHash(view, id, reservationId);

    assertEquals(result, expected);
  }

  // ##################################################################
  // # END: History/Hash
  // ##################################################################

  @Test
  public void nextRegistration01() {
    final FixedClock clock;
    clock = new FixedClock(2025, 1, 13);

    final LocalDateTime dateTime;
    dateTime = LocalDateTime.of(2025, 1, 1, 0, 0);

    final ZoneOffset offset;
    offset = ZoneOffset.UTC;

    final Instant epoch;
    epoch = dateTime.toInstant(offset);

    final FixedGenerator generator;
    generator = new FixedGenerator(12345L);

    final AppReservationGen r;
    r = new AppReservationGen(clock, epoch, generator);

    test(r, "11111111110001101000010000000000000000011000000111001");

    clock.offset = Duration.ofMillis(1);

    test(r, "11111111110001101000010000000100000000011000000111001");

    clock.offset = Duration.ofMillis(2);

    test(r, "11111111110001101000010000001000000000011000000111001");

    clock.offset = Duration.ofMillis(3);

    test(r, "11111111110001101000010000001100000000011000000111001");
  }

  private void test(AppReservationGen r, String expected) {
    final AppReservation res;
    res = r.next();

    final long value;
    value = res.id();

    final String result;
    result = Long.toBinaryString(value);

    assertEquals(result, expected);
  }

  @Test
  public void testCase01() {
    final Http.Exchange http;
    http = Testing.http(config -> {
      config.method(Http.Method.GET);

      config.path("/index.html");

      config.queryParam("page", "i-do-not-exist");
    });

    assertEquals(
        Testing.handle0(http),

        """
        HTTP/1.1 200 OK
        Date: Mon, 28 Apr 2025 13:01:00 GMT
        Content-Type: text/html; charset=utf-8
        Transfer-Encoding: chunked

        # Something Went Wrong

        """
    );
  }

}