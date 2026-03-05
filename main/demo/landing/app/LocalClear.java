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

import static objectos.way.Media.Bytes.textPlain;

import module java.base;
import module objectos.way;

final class LocalClear implements Http.Handler {

  private static final Note.Int1 CLEAR_RESERVATION = Note.Int1.create(LocalClear.class, "Clear Reservation", Note.INFO);

  private final AppCtx ctx;

  LocalClear(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final int localId;
    localId = 1;

    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

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

    http.ok(
        textPlain("OK\n")
    );
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