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

import demo.landing.LandingDemo;
import java.time.LocalDateTime;
import java.util.Optional;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Sql;

final class Confirm implements Kino.GET, Kino.POST {

  private final Kino.Ctx ctx;

  Confirm(Kino.Ctx ctx) {
    this.ctx = ctx;
  }

  public static Html.Component create(Kino.Ctx ctx, Sql.Transaction trx, long reservationId) {
    final Confirm action;
    action = new Confirm(ctx);

    return action.view(trx, reservationId);
  }

  @Override
  public final Html.Component get(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final Kino.Query query;
    query = http.get(Kino.Query.class);

    long reservationId;
    reservationId = query.id();

    return view(trx, reservationId);
  }

  @Override
  public final Kino.PostResult post(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final ConfirmData data;
    data = ConfirmData.parse(http);

    final LocalDateTime today;
    today = ctx.today();

    final Sql.Update ticketResult;
    ticketResult = data.persistTicket(trx, today);

    return switch (ticketResult) {
      case Sql.UpdateFailed _ -> {

        throw new UnsupportedOperationException("Implement me");

      }

      case Sql.UpdateSuccess _ -> {
        final long reservationId;
        reservationId = data.reservationId();

        final String href;
        href = ctx.href(Page.TICKET, reservationId);

        yield LandingDemo.redirect(href);
      }
    };
  }

  private Html.Component view(Sql.Transaction trx, long reservationId) {
    final Optional<ConfirmDetails> maybe;
    maybe = ConfirmDetails.queryOptional(trx, reservationId);

    if (maybe.isEmpty()) {
      return NotFound.create();
    }

    final ConfirmDetails details;
    details = maybe.get();

    return Shell.create(shell -> {
      shell.app = new ConfirmView(ctx, details);

      shell.sources(
        //          Source.Confirm,
      //          Source.ConfirmData,
      //          Source.ConfirmDetails,
      //          Source.ConfirmView
      );
    });
  }

}