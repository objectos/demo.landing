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

import java.util.Arrays;
import objectos.way.Http;
import objectos.way.Sql;
import objectos.way.Web;

/**
 * Represents the user submitted data.
 */
record SeatsData(boolean wayRequest, long reservationId, int screenId, int[] selection) {

  public static SeatsData parse(Http.Exchange http) {
    final Kino.Query query;
    query = http.get(Kino.Query.class);

    final Web.FormData form;
    form = Web.FormData.parse(http);

    return new SeatsData(
        wayRequest(http),

        query.id(),

        query.aux(),

        form.getAllAsInt("seat", Integer.MIN_VALUE).distinct().toArray()
    );
  }

  private static boolean wayRequest(Http.Exchange http) {
    final String maybe;
    maybe = http.header(Http.HeaderName.WAY_REQUEST);

    return "true".equals(maybe);
  }

  @Override
  public final String toString() {
    return String.format(
        "SeatsData[wayRequest=%s, reservationId=%d, screenId=%d, selection=%s]",
        wayRequest, reservationId, screenId, Arrays.toString(selection)
    );
  }

  final void clearTmpSelection(Sql.Transaction trx) {
    // clear this user's current tmp selection

    trx.sql("""
    delete from
      TMP_SELECTION
    where
      RESERVATION_ID = ?
    """);

    trx.add(reservationId);

    trx.update();
  }

  final Sql.BatchUpdate persistTmpSelection(Sql.Transaction trx) {
    clearTmpSelection(trx);

    // insert the data.
    // it will fail if either one is true:
    // 1) invalid RESERVATION_ID
    // 2) invalid SEAT_ID
    // 3) invalid SCREEN_ID
    // 4) SEAT_ID and SCREEN_ID are valid but SEAT_ID does not belong to SCREEN_ID

    trx.sql("""
    insert into
      TMP_SELECTION (RESERVATION_ID, SEAT_ID, SCREEN_ID)
    values
      (?, ?, ?)
    """);

    for (int seatId : selection) {
      trx.add(reservationId);

      trx.add(seatId);

      trx.add(screenId);

      trx.addBatch();
    }

    return trx.batchUpdateWithResult();
  }

  final void clearUserSelection(Sql.Transaction trx) {
    // clear this user's current selection.
    // does nothing (hopefully) if RESERVATION_ID refers to an already sold ticket

    trx.sql("""
    delete from SELECTION
    where RESERVATION_ID in (
      select
        RESERVATION_ID
      from
        RESERVATION
      where
        RESERVATION_ID = ?
        and RESERVATION.TICKET_TIME is null
    )
    """);

    trx.add(reservationId);

    trx.update();
  }

  final Sql.Update persisUserSelection(Sql.Transaction trx) {
    clearUserSelection(trx);

    // persist this user's selection.
    // it will fail if:
    // 1) we try to insert an already selected seat (unique constraint)
    //
    // it will select only a subset (possibly empty) if:
    // 1) RESERVATION_ID refers to an already sold ticket
    // 2) SEAT is from a different screen that the current SHOW

    trx.sql("""
    insert into
      SELECTION (RESERVATION_ID, SEAT_ID, SHOW_ID)
    select
      TMP_SELECTION.RESERVATION_ID,
      TMP_SELECTION.SEAT_ID,
      SHOW.SHOW_ID
    from
      TMP_SELECTION
      join RESERVATION on TMP_SELECTION.RESERVATION_ID = RESERVATION.RESERVATION_ID
      join SHOW on RESERVATION.SHOW_ID = SHOW.SHOW_ID
      join SCREENING on SHOW.SCREENING_ID = SCREENING.SCREENING_ID
      join SEAT on TMP_SELECTION.SEAT_ID = SEAT.SEAT_ID
    where
      RESERVATION.RESERVATION_ID = ?
      and RESERVATION.TICKET_TIME is null
      and SCREENING.SCREEN_ID = SEAT.SCREEN_ID
    """);

    trx.add(reservationId);

    return trx.updateWithResult();
  }

  final int seats() {
    return selection.length;
  }

}