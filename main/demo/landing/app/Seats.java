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
import demo.landing.app.Kino.Query;
import java.util.Optional;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Sql;

final class Seats implements Kino.GET, Kino.POST {

  static final Note.Ref1<SeatsData> DATA_READ = Note.Ref1.create(Seats.class, "Read", Note.DEBUG);

  private final Kino.Ctx ctx;

  Seats(Kino.Ctx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final Html.Component get(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final Kino.Query query;
    query = http.get(Kino.Query.class);

    final int state;
    state = query.aux();

    return switch (state) {
      case SeatsView.DEFAULT -> {
        final int showId;
        showId = query.idAsInt();

        final Optional<SeatsShow> maybeShow;
        maybeShow = SeatsShow.queryOptional(trx, showId);

        if (maybeShow.isEmpty()) {
          yield NotFound.create();
        }

        final long reservationId;
        reservationId = ctx.nextReservation();

        trx.sql("""
        insert into
          RESERVATION (RESERVATION_ID, SHOW_ID)
        values
          (?, ?)
        """);

        trx.param(reservationId);

        trx.param(showId);

        trx.update();

        final SeatsShow show;
        show = maybeShow.get();

        final SeatsGrid grid;
        grid = SeatsGrid.query(trx, reservationId);

        yield view(state, reservationId, show, grid);
      }

      case SeatsView.BACK -> getBackButton(trx, query);

      case SeatsView.BOOKED, SeatsView.EMPTY, SeatsView.LIMIT -> {
        final long reservationId;
        reservationId = query.id();

        final SeatsShow show;
        show = SeatsShow.queryReservation(trx, reservationId);

        final SeatsGrid grid;
        grid = SeatsGrid.query(trx, reservationId);

        yield view(state, reservationId, show, grid);
      }

      default -> NotFound.create();
    };
  }

  private Html.Component getBackButton(Sql.Transaction trx, Query query) {
    final long reservationId;
    reservationId = query.id();

    final Optional<SeatsShow> maybeShow;
    maybeShow = SeatsShow.queryBackButton(trx, reservationId);

    if (maybeShow.isEmpty()) {
      return NotFound.create();
    }

    final SeatsShow show;
    show = maybeShow.get();

    final SeatsGrid grid;
    grid = SeatsGrid.query(trx, reservationId);

    return view(SeatsView.DEFAULT, reservationId, show, grid);
  }

  @Override
  public final Kino.PostResult post(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final SeatsData data;
    data = SeatsData.parse(http);

    ctx.send(DATA_READ, data);

    if (data.seats() == 0) {
      // no seats were selected...
      return postState(trx, data, SeatsView.EMPTY);
    }

    if (data.seats() > 6) {
      // too many seats were selected...
      return postState(trx, data, SeatsView.LIMIT);
    }

    final Sql.BatchUpdate tmpSelectionResult;
    tmpSelectionResult = data.persistTmpSelection(trx);

    return switch (tmpSelectionResult) {
      case Sql.BatchUpdateFailed _ -> tmpSelectionError(trx, data);

      case Sql.BatchUpdateSuccess _ -> tmpSelectionOk(trx, data);
    };
  }

  private Kino.PostResult postState(Sql.Transaction trx, SeatsData data, int state) {
    // just in case, clear this user's selection
    data.clearUserSelection(trx);

    final long reservationId;
    reservationId = data.reservationId();

    return embedView(trx, state, reservationId);
  }

  private Kino.PostResult embedView(Sql.Transaction trx, int state, long reservationId) {
    final SeatsShow show;
    show = SeatsShow.queryReservation(trx, reservationId);

    final SeatsGrid grid;
    grid = SeatsGrid.query(trx, reservationId);

    final Html.Component view;
    view = view(state, reservationId, show, grid);

    return LandingDemo.embedOk(view);
  }

  private Kino.PostResult tmpSelectionError(Sql.Transaction trx, SeatsData data) {
    // clear TMP_SELECTION just in case some of the records were inserted
    data.clearTmpSelection(trx);

    return LandingDemo.embedBadRequest(NotFound.create());
  }

  private Kino.PostResult tmpSelectionOk(Sql.Transaction trx, SeatsData data) {
    final Sql.Update userSelectionResult;
    userSelectionResult = data.persisUserSelection(trx);

    return switch (userSelectionResult) {
      case Sql.UpdateFailed error -> userSelectionError(trx, data, error);

      case Sql.UpdateSuccess ok -> userSelectionOk(trx, data, ok);
    };
  }

  private Kino.PostResult userSelectionError(Sql.Transaction trx, SeatsData data, Sql.UpdateFailed error) {
    // one or more seats were committed by another trx...
    // inform user

    final int state;
    state = SeatsView.BOOKED;

    final long reservationId;
    reservationId = data.reservationId();

    final Kino.Query query;
    query = Page.SEATS.query(reservationId, state);

    final String href;
    href = ctx.href(query);

    return LandingDemo.redirect(href);
  }

  private Kino.PostResult userSelectionOk(Sql.Transaction trx, SeatsData data, Sql.UpdateSuccess ok) {
    int count;
    count = ok.count();

    if (data.seats() != count) {

      // some or possibly all of the seats were not selected.
      // 1) maybe an already sold ticket was submitted
      // 2) seats refer to a different show
      // anyways... bad data

      // clear SELECTION just in case some of the records were inserted
      data.clearUserSelection(trx);

      return LandingDemo.embedBadRequest(NotFound.create());

    }

    // all seats were persisted.
    // go to next screen.

    final long reservationId;
    reservationId = data.reservationId();

    return LandingDemo.redirect(
        ctx.href(Page.CONFIRM, reservationId)
    );

  }

  private Html.Component view(int state, long reservationId, SeatsShow show, SeatsGrid grid) {
    return Shell.create(shell -> {
      shell.app = new SeatsView(ctx, state, reservationId, show, grid);

      shell.sources(
        //          Source.Seats,
      //          Source.SeatsData,
      //          Source.SeatsGrid,
      //          Source.SeatsShow,
      //          Source.SeatsView
      );
    });
  }

}