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

import module objectos.way;

/// The ID of an user making seat reservation.
record AppReservation(long id) {

  private static final String PARAM_NAME = "reservationId";

  static AppReservation parse(Http.Exchange http) {
    final long id;
    id = http.queryParamAsLong(PARAM_NAME, 0L);

    return new AppReservation(id);
  }

  public final boolean isEmpty() {
    return id == 0L;
  }

  @Override
  public final String toString() {
    return PARAM_NAME + "=" + id;
  }

}