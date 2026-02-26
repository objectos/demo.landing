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
import module objectos.way;

/// The seats selection form controller.
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
      handleAlert(http, trx, data, ShowView.Alert.EMPTY);

      return;
    }

    if (data.seats() > 6) {
      // too many seats were selected...
      handleAlert(http, trx, data, ShowView.Alert.LIMIT);

      return;
    }

    final Sql.BatchUpdate tmpSelectionResult;
    tmpSelectionResult = data.persistTmpSelection(trx);

    switch (tmpSelectionResult) {
      case Sql.BatchUpdateFailed _ -> handleTmpSelectionFailed(http, trx, data);

      case Sql.BatchUpdateSuccess _ -> handleTmpSelectionSuccess(http, trx, data);
    }
  }

  private void handleAlert(Http.Exchange http, Sql.Transaction trx, SeatsData data, ShowView.Alert alert) {
    final long reservationId;
    reservationId = data.reservationId();

    final Optional<ShowDetails> maybe;
    maybe = ShowDetails.byReservationId(trx, reservationId);

    final Media view;

    if (maybe.isPresent()) {
      final ShowDetails details;
      details = maybe.get();

      final ShowGrid grid;
      grid = ShowGrid.query(trx, reservationId);

      view = new ShowView(alert, details, grid, reservationId);
    } else {
      view = new NotFoundView();
    }

    http.badRequest(view);
  }

  private void handleTmpSelectionFailed(Http.Exchange http, Sql.Transaction trx, SeatsData data) {
    // clear TMP_SELECTION just in case some of the records were inserted
    data.clearTmpSelection(trx);

    final NotFoundView view;
    view = new NotFoundView();

    http.badRequest(view);
  }

  private void handleTmpSelectionSuccess(Http.Exchange http, Sql.Transaction trx, SeatsData data) {
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

        } else {

          // all seats were persisted.
          // render next screen.

          final long reservationId;
          reservationId = data.reservationId();

          final Optional<ConfirmDetails> maybe;
          maybe = ConfirmDetails.queryOptional(trx, reservationId);

          if (maybe.isPresent()) {
            // renders the confirmation view
            final ConfirmDetails details;
            details = maybe.get();

            final ConfirmView view;
            view = new ConfirmView(details);

            http.ok(view);
          } else {
            // unlikely? in any case, we assume bad data
            final NotFoundView view;
            view = new NotFoundView();

            http.badRequest(view);
          }

        }

      }

      case Sql.UpdateFailed _ -> handleAlert(http, trx, data, ShowView.Alert.BOOKED);
    }
  }

}