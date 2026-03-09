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

import module java.base;
import module objectos.way;

/// Processes the data submitted from the `/confirm` form.
final class ConfirmForm implements Http.Handler {

  private final AppCtx ctx;

  ConfirmForm(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final ConfirmData data;
    data = ConfirmData.parse(http);

    final LocalDateTime now;
    now = ctx.now();

    final Sql.Update ticketResult;
    ticketResult = data.persistTicket(trx, now);

    switch (ticketResult) {
      case Sql.UpdateFailed _ -> {

        throw new UnsupportedOperationException("Implement me");

      }

      case Sql.UpdateSuccess _ -> {
        final AppReservation reservation;
        reservation = data.reservation();

        final String location;
        location = ctx.href(AppView.TICKET, reservation);

        http.seeOther(location);
      }
    }
  }

}