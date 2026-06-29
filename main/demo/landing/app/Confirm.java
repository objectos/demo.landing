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

/// The `/confirm` controller
final class Confirm implements Handler {

  private final AppCtx ctx;

  Confirm(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final AppReservation reservation;
    reservation = AppReservation.parse(req);

    final Optional<ConfirmDetails> maybe;
    maybe = ConfirmDetails.queryOptional(trx, reservation.id());

    if (maybe.isEmpty()) {
      return req;
    }

    final ConfirmDetails details;
    details = maybe.get();

    final String formAction;
    formAction = ctx.href(AppView.CONFIRM, reservation);

    return UiShell.of(opts -> {
      opts.homeAction = ctx.clickAction(AppView.HOME, reservation);

      opts.backAction = ctx.clickAction(AppView.SEATS, details.showId(), reservation);

      opts.main = new ConfirmView(details, formAction);

      opts.sources = List.of(
          Source.Confirm,
          Source.ConfirmData,
          Source.ConfirmDetails,
          Source.ConfirmForm,
          Source.ConfirmView
      );
    });
  }

}