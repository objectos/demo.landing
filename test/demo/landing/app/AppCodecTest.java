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

import java.util.HexFormat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AppCodecTest {

  private final FixedClock clock = new FixedClock(2025, 2, 25);

  private AppCodec codec;

  @BeforeClass
  public void beforeClass() {
    HexFormat hexFormat;
    hexFormat = HexFormat.of();

    byte[] key; // actual value is not important as long it is the same for encode/decode
    key = hexFormat.parseHex("7b9e2a4f6c8d1e3b5a0f7d9c4e2b6a8f1d3c5e7b9a0f2d4c6e8b1a3f5c7d9e0b");

    codec = new AppCodec(clock, key);
  }

  @Test
  public void testCase01() {
    AppUrl q;
    q = codec.decode(null);

    assertEquals(q.page(), AppView.HOME);
    assertEquals(q.id(), 0L);
    assertEquals(q.aux(), 0);

    assertEquals(codec.encode(q), "3c8c90958b1a3f5c7d9e0b7b9e2a4f6c8d");
  }

  @Test
  public void testCase02() {
    AppUrl q;
    q = AppView.HOME.query();

    String result;
    result = codec.encode(q);

    assertEquals(result, "3c8c90958b1a3f5c7d9e0b7b9e2a4f6c8d");
  }

  @Test
  public void testCase03() {
    AppUrl q;
    q = codec.decode("3c8c90958b1a3f5c7d9e0b7b9e2a4f6c8d");

    assertEquals(q.page(), AppView.HOME);
    assertEquals(q.id(), 0L);
    assertEquals(q.aux(), 0);
  }

  @Test
  public void testCase04() {
    AppUrl q;
    q = AppView.CONFIRM.query(40306685673624018L);

    String result;
    result = codec.encode(q);

    assertEquals(result, "3c8c9095881ab06eca0fad2a4c2a4f6c8d");
  }

  @Test
  public void testCase05() {
    AppUrl q;
    q = codec.decode("3c8c9095881ab06eca0fad2a4c2a4f6c8d");

    assertEquals(q.page(), AppView.CONFIRM);
    assertEquals(q.id(), 40306685673624018L);
    assertEquals(q.aux(), 0);
  }

  @Test
  public void testCase06() {
    final long reservationId;
    reservationId = 10902;

    final int screenId;
    screenId = 1031;

    final AppUrl q;
    q = AppView.SEATS.query(reservationId, screenId);

    final String result;
    result = codec.encode(q);

    assertEquals(result, "3c8c9095891a3f5c7d9e0b51082a4f688a");

    final AppUrl decode;
    decode = codec.decode(result);

    assertEquals(decode.page(), AppView.SEATS);
    assertEquals(decode.id(), reservationId);
    assertEquals(decode.aux(), screenId);
  }

}