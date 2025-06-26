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
package demo.landing.local;

import static objectos.way.Media.Bytes.textPlain;

import demo.landing.LandingDemoConfig;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import objectos.way.App;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Sql;

public final class LocalModule implements Http.Routing.Module {

  private static final Note.Int1 CLEAR_RESERVATION = Note.Int1.create(LocalModule.class, "Clear Reservation", Note.INFO);

  private static final Note.Int1 CREATE_SHOW = Note.Int1.create(LocalModule.class, "Create Show", Note.INFO);

  private static final Note.Ref1<Throwable> TRANSACTIONAL = Note.Ref1.create(LocalModule.class, "Transactional", Note.ERROR);

  private final Clock clock;

  private final Sql.Database db;

  private final Note.Sink noteSink;

  LocalModule(App.Injector injector) {
    this(injector.getInstance(LandingDemoConfig.class));
  }

  public LocalModule(LandingDemoConfig config) {
    clock = config.clock;

    db = config.database;

    noteSink = config.noteSink;
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.path("/demo/landing/clear-reservation", path -> {
      path.allow(Http.Method.POST, transactional(this::clearReservation));
    });

    routing.path("/demo/landing/create-show", path -> {
      path.allow(Http.Method.POST, transactional(this::createShow));
    });
  }

  private Http.Handler transactional(Http.Handler handler) {
    return http -> {

      final Sql.Transaction maybeTesting;
      maybeTesting = http.get(Sql.Transaction.class);

      if (maybeTesting != null) {

        handler.handle(http);

      } else {

        final Sql.Transaction trx;
        trx = db.beginTransaction(Sql.READ_COMMITED);

        try {
          trx.sql("set schema CINEMA");

          trx.update();

          http.set(Sql.Transaction.class, trx);

          handler.handle(http);

          trx.commit();
        } catch (Throwable t) {
          noteSink.send(TRANSACTIONAL, t);

          throw trx.rollbackAndWrap(t);
        } finally {
          trx.close();
        }

      }

    };
  }

  // LOCAL_ID = 1
  // VisibleForTesting
  final void clearReservation(Http.Exchange http) {
    final int localId;
    localId = 1;

    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final LocalDateTime now;
    now = LocalDateTime.now(clock);

    trx.sql("""
    delete from RESERVATION
    where
      datediff(minute, RESERVATION_TIME, ?) > 5
      and TICKET_TIME is null
    """);

    trx.param(now);

    int count;
    count = trx.update();

    noteSink.send(CLEAR_RESERVATION, count);

    log(trx, localId);

    http.ok(
        textPlain("OK\n")
    );
  }

  // LOCAL_ID = 2
  // VisibleForTesting
  final void createShow(Http.Exchange http) {
    final int localId;
    localId = 2;

    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final LocalDate now;
    now = LocalDate.now(clock);

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

    trx.param(now);

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

    trx.format(now);

    int count;
    count = trx.update();

    noteSink.send(CREATE_SHOW, count);

    log(trx, localId);

    http.ok(
        textPlain("OK\n")
    );
  }

  private void log(Sql.Transaction trx, int id) {
    final LocalDateTime now;
    now = LocalDateTime.now(clock);

    trx.sql("""
    insert into LOCAL_LOG (LOCAL_ID, LOCAL_TIME) values (?, ?)
    """);

    trx.param(id);

    trx.param(now);

    trx.update();
  }

}