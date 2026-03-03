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

import module java.base;
import module objectos.way;

/// The "/seats/{id}" controller.
final class Seats extends AppTransactional {

  private final AppReservationGen reservationGen;

  Seats(App.Injector injector) {
    super(injector);

    reservationGen = injector.getInstance(AppReservationGen.class);
  }

  @Override
  final void handle(Http.Exchange http, Sql.Transaction trx) {
    final int showId;
    showId = http.pathParamAsInt("id", Integer.MIN_VALUE);

    final Optional<SeatsDetails> maybeDetails;
    maybeDetails = SeatsDetails.byId(trx, showId);

    if (maybeDetails.isEmpty()) {
      final NotFoundView view;
      view = new NotFoundView();

      http.notFound(view);

      return;
    }

    AppReservation reservation;
    reservation = AppReservation.parse(http);

    if (reservation.isEmpty()) {
      reservation = reservationGen.next();

      trx.sql("""
      insert into
        RESERVATION (RESERVATION_ID, SHOW_ID)
      values
        (?, ?)
      """);

      trx.param(reservation.id());

      trx.param(showId);

      trx.update();

      final SeatsDetails details;
      details = maybeDetails.get();

      final SeatsGrid grid;
      grid = SeatsGrid.query(trx, reservation.id());

      final SeatsView view;
      view = new SeatsView(reservation, details, grid);

      http.ok(view);

      return;
    }

    throw new UnsupportedOperationException("Implement me");
  }

}