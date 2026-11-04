/*
 * Copyright (C) 2024-2026 Objectos Software LTDA.
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

/// Renders the tickets bought by the user.
final class TicketView extends Template {

  private final NumberFormat formatter = DecimalFormat.getCurrencyInstance();

  private final TicketModel ticket;

  TicketView(TicketModel ticket) {
    this.ticket = ticket;
  }

  @Override
  protected final void render() {
    t.h1("Ticket #" + ticket.id());

    h2(text("Thank You"));

    p(text("Here's your receipt"));

    div(
        css("""
        position:relative
        """),

        UiIcon.RECEIPT.css("""
        border:1px_solid_var(--color-border)
        border-radius:9999px
        height:auto
        inset:-80rx_0_auto_auto
        padding:16rx
        position:absolute
        stroke-width:1rx
        width:80rx
        """)
    );

    dl(
        css("""
        border:1px_solid_var(--color-border)
        display:flex
        flex-direction:column
        gap:4rx
        margin:32rx_0_0
        padding:16rx

        &_div/display:flex
        &_div/justify-content:space-between

        &_dd/font-weight:500
        """),

        div(
            dt(text("Ammount Paid")),
            dd(t.prop("Ammount Paid", format(ticket.ammountPaid())))
        ),

        div(
            dt(text("Purchase Time")),
            dd(t.prop("Purchase Time", ticket.purchaseTime()))
        )
    );

    h2(t.h2("Tickets"));

    p(text(ticket.singular() ? "Here's your ticket" : "Here are your tickets"));

    div(
        css("""
        display:flex
        flex-direction:column
        gap:16rx
        padding:32rx_0_0
        """),

        f(this::renderTickets)
    );
  }

  private void renderTickets() {
    for (var item : ticket.items()) {
      div(
          css("""
          border:1px_solid_var(--color-border)
          display:flex
          justify-content:space-between
          padding:16rx
          """),

          UiIcon.TICKET.css("""
          height:auto
          margin-right:16rx
          padding:16rx
          stroke-width:1rx
          width:80rx
          """),

          ul(
              css("""
              font-size:14rx
              flex:1
              """),

              li(text(testableCell(ticket.title(), 8))),
              li(text(testableCell(ticket.date(), 10))),
              li(text(testableCell(ticket.time(), 6))),
              li(text(testableCell(ticket.screen(), 8)))
          ),

          div(
              css("""
              align-items:end
              display:flex
              flex-direction:column
              justify-content:space-around
              """),

              div(
                  css("""
                  font-size:20rx
                  font-weight:300
                  """),

                  text("Seat "),
                  text(testableCell(item.seat(), 5))
              ),

              div(
                  css("""
                  font-size:14rx
                  font-weight:600
                  """),

                  text(testableCell(format(item.price()), 6))
              )
          )
      );

      testableNewLine();
    }
  }

  private String format(double price) {
    return formatter.format(price);
  }

  private String testableCell(String value, int width) {
    t.cell(width, value);

    return value;
  }

  private void testableNewLine() {
    t.row();
  }

}