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

import java.time.LocalDateTime;
import objectos.http.Content;
import objectos.http.Handler;
import objectos.http.MediaType;
import objectos.http.Request;
import objectos.http.Result;
import objectos.way.Note;
import objectos.way.Sql;

final class LocalClear implements Handler {

  private static final Note.Int1 CLEAR_RESERVATION = Note.Int1.create(LocalClear.class, "Clear Reservation", Note.INFO);

  private final AppCtx ctx;

  LocalClear(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Result handle(Request req) {
    final int localId;
    localId = 1;

    final Sql.Transaction trx;
    trx = req.attr(Sql.Transaction.class);

    final LocalDateTime now;
    now = ctx.now();

    trx.sql("""
    delete from RESERVATION
    where
      datediff(minute, RESERVATION_TIME, ?) > 5
      and TICKET_TIME is null
    """);

    trx.param(now);

    final int count;
    count = trx.update();

    ctx.send(CLEAR_RESERVATION, count);

    log(trx, localId);

    return Content.of(MediaType.TEXT_PLAIN, "OK\n");
  }

  private void log(Sql.Transaction trx, int id) {
    final LocalDateTime now;
    now = ctx.now();

    trx.sql("""
    insert into LOCAL_LOG (LOCAL_ID, LOCAL_TIME) values (?, ?)
    """);

    trx.param(id);

    trx.param(now);

    trx.update();
  }

}