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

import java.time.LocalDate;
import java.time.LocalDateTime;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Sql;

final class LocalCreate implements Http.Handler {

  private static final Note.Int1 CREATE_SHOW = Note.Int1.create(LocalCreate.class, "Create Show", Note.INFO);

  private final AppCtx ctx;

  LocalCreate(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final int localId;
    localId = 2;

    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final LocalDate today;
    today = ctx.today();

    trx.sql("""
    select
      count(*)
    from
      LOCAL_LOG
    where
      LOCAL_ID = ?
      and cast(LOCAL_TIME as date) = ?
    """);

    trx.param(localId);

    trx.param(today);

    Integer _executions;
    _executions = trx.querySingle((rs, idx) -> rs.getInt(idx++));

    int executions;
    executions = _executions.intValue();

    if (executions > 0) {
      http.ok(
          textPlain("Skipped: already executed\\n")
      );

      return;
    }

    trx.sql("""
    insert into
      SHOW (SCREENING_ID, SHOWDATE, SHOWTIME, SEAT_PRICE)
    select
      SCREENING_ID,
      dateadd (day, 2, '%1$s'),
      SCREENING_TIME,
      SEAT_PRICE
    from
      SCREENING_TIME
    order by
      1,
      2,
      3
    """);

    trx.format(today);

    int count;
    count = trx.update();

    ctx.send(CREATE_SHOW, count);

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