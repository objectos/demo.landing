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

import java.util.List;
import java.util.Optional;
import objectos.http.Handler;
import objectos.http.Request;
import objectos.http.Result;
import objectos.way.Sql;

/// The `/seats/{id}` controller.
final class Seats implements Handler {

  private final AppCtx ctx;

  Seats(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final int id;
    id = req.pathParamAsInt("id", Integer.MIN_VALUE);

    final int showId;
    showId = id & 0xFFFF;

    final Optional<SeatsDetails> maybeDetails;
    maybeDetails = SeatsDetails.byId(trx, showId);

    if (maybeDetails.isEmpty()) {
      return req;
    }

    final AppReservation reservation;
    reservation = AppReservation.parse(req, () -> generator(trx, showId));

    final int alertId;
    alertId = (id >>> 16);

    final SeatsAlert alert;
    alert = SeatsAlert.of(alertId);

    final SeatsDetails details;
    details = maybeDetails.get();

    final SeatsGrid grid;
    grid = SeatsGrid.query(trx, reservation.id());

    final String formAction;
    formAction = ctx.href(AppView.SEATS, showId, reservation);

    return UiShell.of(opts -> {
      opts.backAction = ctx.clickAction(AppView.MOVIE, details.movieId(), reservation);

      opts.homeAction = ctx.clickAction(AppView.HOME, reservation);

      opts.main = new SeatsView(alert, details, grid, formAction);

      opts.sources = List.of(
          Source.Seats,
          Source.SeatsAlert,
          Source.SeatsData,
          Source.SeatsDetails,
          Source.SeatsForm,
          Source.SeatsGrid,
          Source.SeatsView
      );
    });
  }

  private long generator(Sql.Transaction trx, int showId) {
    final long rid;
    rid = ctx.nextReservation();

    trx.sql("""
      insert into
        RESERVATION (RESERVATION_ID, SHOW_ID)
      values
        (?, ?)
      """);

    trx.param(rid);

    trx.param(showId);

    trx.update();

    return rid;
  }

}