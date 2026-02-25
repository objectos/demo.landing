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

final class Show implements Http.Handler {

  private final AppReservation reservation;

  Show(AppReservation reservation) {
    this.reservation = reservation;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final int showId;
    showId = http.pathParamAsInt("id", Integer.MIN_VALUE);

    final Optional<ShowDetails> maybeDetails;
    maybeDetails = ShowDetails.queryOptional(trx, showId);

    if (maybeDetails.isEmpty()) {
      final NotFoundView view;
      view = new NotFoundView();

      http.notFound(view);

      return;
    }

    final long reservationId;
    reservationId = reservation.next();

    trx.sql("""
    insert into
      RESERVATION (RESERVATION_ID, SHOW_ID)
    values
      (?, ?)
    """);

    trx.param(reservationId);

    trx.param(showId);

    trx.update();

    final ShowDetails details;
    details = maybeDetails.get();

    final ShowGrid grid;
    grid = ShowGrid.query(trx, reservationId);

    final ShowView view;
    view = new ShowView(details, grid, reservationId);

    http.ok(view);
  }

}