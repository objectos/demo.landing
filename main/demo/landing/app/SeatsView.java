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

import module java.base;
import module objectos.way;

final class SeatsView extends UiShell {

  private static final String FORM_ID = "seats-form";

  private final AppReservation reservation;

  private final SeatsAlert alert;

  private final SeatsDetails details;

  private final SeatsGrid grid;

  SeatsView(AppReservation reservation, SeatsDetails details, SeatsGrid grid) {
    this(reservation, null, details, grid);
  }

  SeatsView(AppReservation reservation, SeatsAlert alert, SeatsDetails details, SeatsGrid grid) {
    this.reservation = reservation;

    this.alert = alert;

    this.details = details;

    this.grid = grid;
  }

  @Override
  final List<SourceModel> viewSources() {
    return List.of(
    );
  }

  @Override
  final void renderMain() {
    final String backUrl;
    backUrl = reservation.to(AppView.MOVIE, details.movieId());

    backLink(backUrl);

    // this node is for testing only, it is not rendered in the final HTML
    testableH1("Show details");

    h2(testableField("title", details.title()));

    p("Please choose your seats");

    if (alert != null) {
      testableField("alert", alert.name());

      renderAlert(alert.msg);
    }

    div(
        css("""
        border:1px_solid_var(--color-border)
        display:flex
        gap:24rx
        margin:32rx_0
        overflow-x:auto
        padding:32rx_24rx
        """),

        f(this::renderDetails),

        f(this::renderSeats)
    );
  }

  private void renderAlert(String msg) {
    div(
        css("""
        align-items:center
        background-color:var(--color-blue-100)
        border-left:3px_solid_var(--color-blue-800)
        display:flex
        font-size:14rx
        line-height:16rx
        margin:16rx_0
        padding:16rx
        """),

        c(
            UiIcon.INFO.css("""
            height:20rx
            width:auto
            padding-right:16rx
            """)
        ),

        div(
            css("""
            flex:1
            """),

            text(msg)
        )
    );
  }

  private void renderDetails() {
    div(
        css("""
        display:flex
        flex-direction:column
        font-size:14rx
        gap:16rx
        """),

        renderDetailsItem(UiIcon.CALENDAR_CHECK, "date", details.date()),

        renderDetailsItem(UiIcon.CLOCK, "time", details.time()),

        renderDetailsItem(UiIcon.PROJECTOR, "screen", details.screen())
    );
  }

  private Html.Instruction.OfElement renderDetailsItem(UiIcon icon, String name, String value) {
    return div(
        css("""
        align-items:center
        display:flex
        flex-direction:column
        gap:4rx
        """),

        c(
            icon.css("""
            height:auto
            stroke:icon
            width:20rx
            """)
        ),

        span(
            css("""
            text-align:center
            width:6rch
            """),

            text(testableField(name, value))
        )
    );
  }

  private void renderSeats() {
    // this node is for testing only, it is not rendered in the final HTML
    testableH1("Seats");

    div(
        css("""
        display:flex
        flex-direction:column
        flex-grow:1
        justify-content:start
        """),

        f(this::renderSeatsScreen),

        f(this::renderSeatsForm),

        f(this::renderSeatsAction)
    );
  }

  private void renderSeatsScreen() {
    svg(
        css("""
        display:block
        margin:0_auto
        max-height:60rx
        max-width:400rx
        min-height:0
        min-width:0
        stroke:icon
        width:100%
        """),

        width("400"), height("60"), viewBox("0 0 400 60"), xmlns("http://www.w3.org/2000/svg"),
        path(d("M 0 50 Q 200 0 400 50")), fill("none"), strokeWidth("1.25")
    );

    p(
        css("""
        font-size:14rx
        inset:-32rx_0_auto
        position:relative
        text-align:center
        """),

        text("Screen")
    );
  }

  private void renderSeatsForm() {
    form(
        id(FORM_ID),

        action("/demo.landing/seats"),

        css("""
        aspect-ratio:1.15
        display:grid
        flex-grow:1
        gap:8rx
        grid-template-columns:repeat(10,1fr)
        grid-template-rows:repeat(10,minmax(20rx,1fr))
        margin:0_auto
        max-width:400rx
        width:100%
        """),

        method("post"),

        onsubmit(submit()),

        input(
            type("hidden"),
            name("reservationId"),
            value(testableField("reservationId", Long.toString(reservation.id())))),

        input(
            type("hidden"),
            name("showId"),
            value(testableField("showId", Integer.toString(details.showId())))),

        input(
            type("hidden"),
            name("screenId"),
            value(testableField("screenId", Integer.toString(details.screenId())))),

        f(this::renderSeatsFormGrid)
    );
  }

  private void renderSeatsFormGrid() {
    for (SeatsGrid.Seat seat : grid) {
      final int seatId;
      seatId = seat.seatId();

      if (seatId < 0) {
        div();
      } else {
        final String seatIdValue;
        seatIdValue = Integer.toString(seatId);

        input(
            css("""
            cursor:pointer

            disabled:cursor:default
            """),

            name("seat"),

            type("checkbox"),

            seat.checked() ? checked : noop(),

            seat.reserved() ? disabled : noop(),

            value(seatIdValue)
        );

        if (seat.checked()) {
          testableField("checked", seatIdValue);
        }
      }
    }
  }

  private void renderSeatsAction() {
    div(
        css("""
        display:flex
        justify-content:end
        padding-top:64rx
        margin:0_auto
        max-width:400rx
        width:100%
        """),

        button(
            PRIMARY,

            form(FORM_ID),

            type("submit"),

            text("Book seats")
        )
    );
  }

}