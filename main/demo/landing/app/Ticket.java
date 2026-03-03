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

import java.util.Optional;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Sql;

final class Ticket {

  Ticket() {}

  public static Html.Component create(Sql.Transaction trx, ConfirmData data) {
    final Ticket action;
    action = new Ticket();

    final long ticketId;
    ticketId = data.reservationId();

    return action.view(trx, ticketId);
  }

  public final Html.Component get(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final AppReservation query;
    query = http.get(AppReservation.class);

    final long ticketId;
    ticketId = query.id();

    return view(trx, ticketId);
  }

  @SuppressWarnings("unused")
  private Html.Component view(Sql.Transaction trx, long ticketId) {
    final Optional<TicketModel> maybe;
    maybe = TicketModel.queryOptional(trx, ticketId);

    if (maybe.isPresent()) {
      final TicketModel model;
      model = maybe.get();

      throw new UnsupportedOperationException("Implement me");
    } else {
      throw new UnsupportedOperationException("Implement me");
      //return NotFound.create();
    }
  }

}