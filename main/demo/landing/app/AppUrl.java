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

import objectos.way.Http;

/// Decoded value of the URL's fragment.
record AppUrl(AppView page, long id, int aux) {

  static final Http.HeaderName DEMO_HASH = Http.HeaderName.of("Demo-Hash");

  static AppUrl parse(Http.Exchange http) {
    final String demoHash;
    demoHash = http.header(DEMO_HASH);

    final AppUrl state;

    if (demoHash != null) {
      throw new UnsupportedOperationException("Implement me");
    } else {

      final long reservationId;
      reservationId = http.queryParamAsLong("reservationId", 0L);

      final int id;
      id = http.pathParamAsInt("id", Integer.MIN_VALUE);

      state = new AppUrl(null, reservationId, id);
    }

    return state;
  }

  final int idAsInt() {
    return (int) id;
  }

}