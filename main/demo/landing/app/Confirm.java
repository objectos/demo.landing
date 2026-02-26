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

final class Confirm implements Http.Handler {

  private final Clock clock;

  Confirm(Clock clock) {
    this.clock = clock;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final ConfirmData data;
    data = ConfirmData.parse(http);

    final LocalDateTime today;
    today = LocalDateTime.now(clock);

    final Sql.Update ticketResult;
    ticketResult = data.persistTicket(trx, today);

    switch (ticketResult) {
      case Sql.UpdateFailed _ -> {

        throw new UnsupportedOperationException("Implement me");

      }

      case Sql.UpdateSuccess _ -> {
        final long reservationId;
        reservationId = data.reservationId();

        final Optional<TicketModel> maybe;
        maybe = TicketModel.queryOptional(trx, reservationId);

        final TicketModel model;
        model = maybe.get();

        final TicketView view;
        view = new TicketView(model);

        http.ok(view);
      }
    }
  }

}