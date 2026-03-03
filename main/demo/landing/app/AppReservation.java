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

/// The ID of an user making seat reservation.
record AppReservation(long id) {

  static final AppReservation EMPTY = new AppReservation(0L);

  static AppReservation parse(Http.Exchange http) {
    return new AppReservation(
        http.queryParamAsLong("reservationId", 0L)
    );
  }

  public final boolean isEmpty() {
    return id == 0L;
  }

  public final String to(AppView view) {
    return "/demo.landing/" + view.slug + this;
  }

  public final String to(AppView view, int id) {
    return "/demo.landing/" + view.slug + "/" + id + this;
  }

  @Override
  public final String toString() {
    return "?reservationId=" + id;
  }

}