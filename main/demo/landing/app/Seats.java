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

import module objectos.way;

final class Seats implements Http.Handler {

  static final Note.Ref1<SeatsData> DATA_READ = Note.Ref1.create(Seats.class, "Read", Note.DEBUG);

  private final Note.Sink noteSink;

  Seats(Note.Sink noteSink) {
    this.noteSink = noteSink;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final SeatsData data;
    data = SeatsData.parse(http);

    noteSink.send(DATA_READ, data);

    if (data.seats() == 0) {
      // no seats were selected...
      throw new UnsupportedOperationException("Implement me");
    }

    if (data.seats() > 6) {
      // too many seats were selected...
      throw new UnsupportedOperationException("Implement me");
    }

    final Sql.BatchUpdate tmpSelectionResult;
    tmpSelectionResult = data.persistTmpSelection(trx);

    switch (tmpSelectionResult) {
      case Sql.BatchUpdateSuccess _ -> {
        final Sql.Update userSelectionResult;
        userSelectionResult = data.persisUserSelection(trx);

        switch (userSelectionResult) {
          case Sql.UpdateSuccess ok -> {
            int count;
            count = ok.count();

            if (data.seats() != count) {

              // some or possibly all of the seats were not selected.
              // 1) maybe an already sold ticket was submitted
              // 2) seats refer to a different show
              // anyways... bad data

              // clear SELECTION just in case some of the records were inserted
              data.clearUserSelection(trx);

              final NotFoundView view;
              view = new NotFoundView();

              http.badRequest(view);

              return;
            }

            // all seats were persisted.
            // go to next screen.

            final long reservationId;
            reservationId = data.reservationId();

            throw new UnsupportedOperationException("Implement me");
          }

          case Sql.UpdateFailed _ -> userSelectionError(http, data);
        }
      }

      case Sql.BatchUpdateFailed _ -> {
        // clear TMP_SELECTION just in case some of the records were inserted
        data.clearTmpSelection(trx);

        final NotFoundView view;
        view = new NotFoundView();

        http.badRequest(view);
      }
    }
  }

  private void userSelectionError(Http.Exchange http, SeatsData data) {
    // one or more seats were committed by another trx...
    // inform user

    final int state;
    state = SeatsView.BOOKED;

    final long reservationId;
    reservationId = data.reservationId();

    throw new UnsupportedOperationException("Implement me");

  }

}