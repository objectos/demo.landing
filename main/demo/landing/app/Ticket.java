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
import objectos.script.Js;
import objectos.way.Http;
import objectos.way.Sql;

final class Ticket implements Http.Handler {

  Ticket() {}

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final AppReservation reservation;
    reservation = AppReservation.parse(http);

    final Optional<TicketModel> maybe;
    maybe = TicketModel.queryOptional(trx, reservation.id());

    if (maybe.isEmpty()) {
      return;
    }

    final TicketModel ticket;
    ticket = maybe.get();

    final UiShell shell;
    shell = UiShell.of(opts -> {
      opts.homeAction = Js.byId(AppCtx.SHELL).render("/demo.landing/home", render -> {
        render.history("/index.html");
      });

      opts.main = new TicketView(ticket);

      opts.sources = List.of(
          Source.Ticket,
          Source.TicketModel,
          Source.TicketView
      );
    });

    http.ok(shell);
  }

}