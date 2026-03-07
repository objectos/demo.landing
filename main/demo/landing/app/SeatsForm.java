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

/// The seats selection form controller.
final class SeatsForm implements Http.Handler {

  static final Note.Ref1<SeatsData> DATA_READ = Note.Ref1.create(SeatsForm.class, "Read", Note.DEBUG);

  private final AppCtx ctx;

  SeatsForm(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final SeatsData data;
    data = SeatsData.parse(http);

    ctx.send(DATA_READ, data);

    if (data.seats() == 0) {
      // no seats were selected...
      handleAlert(http, trx, data, SeatsAlert.EMPTY);

      return;
    }

    if (data.seats() > 6) {
      // too many seats were selected...
      handleAlert(http, trx, data, SeatsAlert.LIMIT);

      return;
    }

    final Sql.BatchUpdate tmpSelectionResult;
    tmpSelectionResult = data.persistTmpSelection(trx);

    switch (tmpSelectionResult) {
      case Sql.BatchUpdateFailed _ -> handleTmpSelectionFailed(http, trx, data);

      case Sql.BatchUpdateSuccess _ -> handleTmpSelectionSuccess(http, trx, data);
    }
  }

  private void handleAlert(Http.Exchange http, Sql.Transaction trx, SeatsData data, SeatsAlert alert) {
    // just in case, clear this user's selection
    data.clearUserSelection(trx);

    final int showId;
    showId = data.showId();

    final int alertId;
    alertId = alert.id();

    final int id;
    id = (alertId << 16) | showId;

    final AppReservation reservation;
    reservation = data.reservation();

    final String seatsUrl;
    seatsUrl = ctx.href(AppView.SEATS, id, reservation);

    http.seeOther(seatsUrl);
  }

  private void handleTmpSelectionFailed(Http.Exchange http, Sql.Transaction trx, SeatsData data) {
    // clear TMP_SELECTION just in case some of the records were inserted
    data.clearTmpSelection(trx);

    // insertion failed => bad data

    http.badRequest(Media.Bytes.textPlain("Bad data"));
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

          http.badRequest(Media.Bytes.textPlain("Bad data"));

        } else {

          // all seats were persisted.
          // render next screen.

          final AppReservation reservation;
          reservation = data.reservation();

          final String redirectUrl;
          redirectUrl = ctx.href(AppView.CONFIRM, reservation);

          http.seeOther(redirectUrl);

        }

      }

      case Sql.UpdateFailed _ -> handleAlert(http, trx, data, SeatsAlert.BOOKED);
    }
  }

}