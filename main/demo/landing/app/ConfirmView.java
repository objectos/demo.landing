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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import objectos.script.Js;
import objectos.way.Html;

final class ConfirmView extends UiShell {

  private final ConfirmDetails details;

  private final NumberFormat formatter = DecimalFormat.getCurrencyInstance();

  ConfirmView(ConfirmDetails details) {
    this.details = details;
  }

  @Override
  final List<SourceModel> viewSources() {
    return List.of();
  }

  @Override
  final void renderMain() {
    final long reservationId;
    reservationId = details.reservationId();

    backLink("/demo.landing/seats/0?reservationId=" + reservationId);

    h2("Your Order");

    // testable node only
    testableH1("Order #" + reservationId);

    p("Please review and confirm your order");

    div(
        css("""
        border:1px_solid_var(--color-border)
        display:flex
        gap:24rx
        margin:32rx_0
        overflow-x:auto
        padding:32rx_24rx

        &_h3/font-size:24rx
        &_h3/font-weight:300
        &_h3/padding-bottom:16rx
        """),

        div(
            css("""
            display:flex
            flex:1.5
            flex-direction:column
            gap:12rx
            """),

            f(this::renderLeft)
        ),

        div(
            css("""
            display:flex
            flex:2
            flex-direction:column
            gap:12rx
            """),

            f(this::renderRight)
        )
    );
  }

  private void renderLeft() {
    h3("Movie");

    renderDetailsItem(UiIcon.FILM, "title", details.title());

    renderDetailsItem(UiIcon.CALENDAR_CHECK, "date", details.date());

    renderDetailsItem(UiIcon.CLOCK, "time", details.time());

    renderDetailsItem(UiIcon.PROJECTOR, "screen", details.screen());
  }

  private Html.Instruction.OfElement renderDetailsItem(UiIcon icon, String name, String value) {
    return div(
        css("""
        display:flex
        gap:12rx
        """),

        c(
            icon.css("""
            height:auto
            width:16rx
            """)
        ),

        span(
            css("""
            font-size:14rx
            line-height:16rx
            """),

            text(testableField(name, value))
        )
    );
  }

  private void renderRight() {
    h3(
        testableH2("Order Details")
    );

    for (var item : details.items()) {
      div(
          css("""
          display:flex
          font-size:14rx
          justify-content:space-between
          line-height:16rx
          """),

          div(
              text("Seat "),
              text(testableCell(item.seat(), 5))
          ),

          div(
              css("""

              """),

              text(testableCell(format(item.price()), 6))
          )
      );

      testableNewLine();
    }

    div(
        css("""
        border-top:1px_solid_var(--color-border)
        display:flex
        font-size:14rx
        justify-content:space-between
        line-height:16rx
        padding-top:12rx
        """),

        div(text(testableCell("Total", 5))),

        div(
            css("""
            font-weight:600
            """),

            text(testableCell(format(details.totalPrice()), 6))
        )
    );

    div(
        css("""
        display:flex
        font-size:14rx
        justify-content:space-between
        line-height:16rx
        margin-top:24rx
        """),

        div("Payment Method"),

        div(
            css("""
            display:flex
            gap:8rx
            """),

            c(
                UiIcon.CREDIT_CARD.css("""
                height:auto
                width:16rx
                """)
            ),

            span("4321")
        )
    );

    testableNewLine();

    final long reservationId;
    reservationId = details.reservationId();

    form(
        css("""
        display:flex
        justify-content:end
        margin-top:24rx
        """),

        action("/demo.landing/confirm"),

        onsubmit(Js.submit()),

        method("post"),

        input(
            type("hidden"),
            name("reservationId"),
            value(testableField("reservationId", Long.toString(reservationId)))),

        button(
            PRIMARY,

            type("submit"),

            text("Confirm")
        )
    );
  }

  private String format(double price) {
    return formatter.format(price);
  }

}