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
import demo.landing.LandingDemoConfig;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import objectos.script.Js;
import objectos.script.JsAction;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Note;

/**
 * Demo entry point.
 */
public final class Kino implements LandingDemo {

  public static final Html.Id SHELL = Html.Id.of("demo.landing");

  /// The default `follow` action.
  ///
  /// As this demo will be embedded in another application, we:
  ///
  /// - disable history; so the browser location is not updated on navigation - -
  /// - disable scroll; so the browse scroll position is not reset on navigation
  static final JsAction FOLLOW = Js.follow(opts -> {
    // disable history
    opts.history(false);

    // disable scroll
    opts.scroll(false);
  });

  private final Ctx ctx;

  private Kino(Ctx ctx) {
    this.ctx = ctx;
  }

  /**
   * Creates a new {@code Kino} instance with the specified configuration.
   */
  public static Kino create(LandingDemoConfig config) {
    Objects.requireNonNull(config, "config == null");

    final Ctx ctx;
    ctx = Ctx.of(config);

    return new Kino(ctx);
  }

  /// Creates a new instance of the demo's `Css.StyleSheet` configuration.
  ///
  /// @return a new instance of the demo's `Css.StyleSheet` configuration
  public static Css.Library styles() {
    return new KinoStyles();
  }

  /**
   * Handles a GET request.
   *
   * <p>
   * Typically this would be a {@code Http.Handler} instance. However, as this
   * will be embedded in another application, we return a HTML component
   * instead.
   */
  @Override
  public final Html.Component get(Http.Exchange http) {
    ctx.decode(http);

    final Page page;
    page = Page.parse(http);

    // based on the 'page' value we create our controller
    final GET controller;
    controller = switch (page) {
      case CONFIRM -> new Confirm(ctx);

      case MOVIE -> new Movie(ctx);

      case NOW_SHOWING -> new NowShowing();

      case SEATS -> new Seats(ctx);

      case TICKET -> new Ticket();

      case BAD_REQUEST -> new NotFound();
    };

    // we intercept all controllers even though
    // not all require DB access strictly speaking
    return ctx.transactional(http, controller);
  }

  /**
   * Handles a POST request.
   */
  @Override
  public final Kino.PostResult post(Http.Exchange http) {
    final Query query;
    query = ctx.decode(http);

    final POST controller;
    controller = switch (query.page) {
      case CONFIRM -> new Confirm(ctx);

      case SEATS -> new Seats(ctx);

      default -> new NotFound();
    };

    return ctx.transactional(http, controller);
  }

  //
  // UI related classes
  //

  /**
   * SVG icons from the Lucide project.
   */
  enum Icon {
    ARROW_LEFT("""
    <path d="m12 19-7-7 7-7"/><path d="M19 12H5"/>"""),

    CALENDAR_CHECK("""
    <path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="m9 16 2 2 4-4"/>"""),

    CLOCK("""
    <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>"""),

    CREDIT_CARD("""
    <rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" x2="22" y1="10" y2="10"/>"""),

    FILM(
        """
    <rect width="18" height="18" x="3" y="3" rx="2"/><path d="M7 3v18"/><path d="M3 7.5h4"/><path d="M3 12h18"/><path d="M3 16.5h4"/><path d="M17 3v18"/><path d="M17 7.5h4"/><path d="M17 16.5h4"/>"""),

    FROWN("""
    <circle cx="12" cy="12" r="10"/><path d="M16 16s-1.5-2-4-2-4 2-4 2"/><line x1="9" x2="9.01" y1="9" y2="9"/><line x1="15" x2="15.01" y1="9" y2="9"/>"""),

    INFO("""
    <circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/>"""),

    PROJECTOR(
        """
    <path d="M5 7 3 5"/><path d="M9 6V3"/><path d="m13 7 2-2"/><circle cx="9" cy="13" r="3"/><path d="M11.83 12H20a2 2 0 0 1 2 2v4a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2v-4a2 2 0 0 1 2-2h2.17"/><path d="M16 16h2"/>"""),

    RECEIPT("""
    <path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1Z"/><path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"/><path d="M12 17.5v-11"/>"""),

    TICKET("""
    <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/><path d="M13 5v2"/><path d="M13 17v2"/><path d="M13 11v2"/>""");

    final String contents;

    Icon(String contents) {
      this.contents = contents;
    }
  }

  /**
   * Base HTML template of the application. Provides a utility methods for
   * rendering UI fragments common to the application.
   */
  static abstract class View extends Html.Template {

    static final Html.ClassName PRIMARY = Html.ClassName.ofText("""
    appearance:none
    background-color:var(--color-btn-primary)
    color:var(--color-btn-primary-text)
    cursor:pointer
    display:flex
    font-size:14rx
    min-height:48rx
    padding:14rx_63rx_14rx_15rx

    active/background-color:var(--color-btn-primary-active)
    hover/background-color:var(--color-btn-primary-hover)
    """);

    //
    // component methods
    //

    /**
     * Renders the "Go Back" link.
     */
    final Html.Instruction.OfElement backLink(Ctx ctx, Page page) {
      testableField("back-link", page.name());

      return backLink(ctx.href(page));
    }

    /**
     * Renders the "Go Back" link.
     */
    final Html.Instruction.OfElement backLink(Ctx ctx, Page page, long id) {
      testableField("back-link", page.name() + ":" + id);

      return backLink(ctx.href(page, id));
    }

    /**
     * Renders the "Go Back" link.
     */
    final Html.Instruction.OfElement backLink(Ctx ctx, Page page, long id, int aux) {
      testableField("back-link", page.name() + ":" + id + ":" + aux);

      Query query = page.query(id, aux);

      return backLink(ctx.href(query));
    }

    /**
     * Renders the "Go Back" link.
     */
    final Html.Instruction.OfElement backLink(String href) {
      return a(
          css("""
          border-radius:9999px
          padding:6rx
          margin:6rx_0_0_-6rx
          position:absolute

          active/background-color:var(--color-btn-ghost-active)
          hover/background-color:var(--color-btn-ghost-hover)
          """),

          onclick(FOLLOW),

          href(href),

          rel("nofollow"),

          icon(
              Kino.Icon.ARROW_LEFT,

              css("""
              height:20rx
              width:20rx
              """)
          )
      );
    }

    /// Renders the "Go Back" link.
    final Html.Instruction.OfElement backLink2(String href) {
      testableField("back-link", href);

      return a(
          css("""
          border-radius:9999px
          padding:6rx
          margin:6rx_0_0_-6rx
          position:absolute

          active/background-color:var(--color-btn-ghost-active)
          hover/background-color:var(--color-btn-ghost-hover)
          """),

          onclick(FOLLOW),

          href(href),

          rel("nofollow"),

          icon(
              Kino.Icon.ARROW_LEFT,

              css("""
              height:20rx
              width:20rx
              """)
          )
      );
    }

    final Html.Instruction.OfAttribute formAction(Ctx ctx, Page page, long id) {
      testableField("action", page.name() + ":" + id);

      return action(ctx.action(page, id));
    }

    final Html.Instruction.OfAttribute formAction(Ctx ctx, Page page, long id, int aux) {
      testableField("action", page.name() + ":" + id + ":" + aux);

      return action(ctx.action(page, id, aux));
    }

    /**
     * Renders a Lucide SVG icon.
     */
    final Html.Instruction icon(Kino.Icon icon, Html.Instruction... more) {
      return svg(
          xmlns("http://www.w3.org/2000/svg"), width("24"), height("24"), viewBox("0 0 24 24"),
          fill("none"), stroke("currentColor"), strokeWidth("2"), strokeLinecap("round"), strokeLinejoin("round"),

          flatten(more),

          raw(icon.contents)
      );
    }

  }

  //
  // Configuration related classes
  //

  /**
   * Application-level context.
   */
  record Ctx(
      Clock clock,

      KinoCodec codec,

      Note.Sink noteSink,

      Reservation reservation,

      Transactional transactional
  ) {

    static Ctx of(LandingDemoConfig config) {
      final Clock clock;
      clock = config.clock;

      final byte[] codecKey;
      codecKey = config.codecKey();

      final KinoCodec codec;
      codec = new KinoCodec(codecKey);

      final Note.Sink noteSink;
      noteSink = config.noteSink;

      final Reservation reservation;
      reservation = new Reservation(clock, config.reservationEpoch, config.reservationRandom);

      final Transactional transactional;
      transactional = new Transactional(config.stage, config.database);

      return new Ctx(clock, codec, noteSink, reservation, transactional);
    }

    final String action(Page page, long id) {
      return action(page, id, 0);
    }

    final String action(Page page, long id, int aux) {
      Query query;
      query = page.query(id, aux);

      String demo;
      demo = codec.encode(query);

      return "/demo/landing?demo=" + demo;
    }

    final String href(Page page) {
      return page == Page.NOW_SHOWING ? "/index.html" : href(page, 0L);
    }

    final String href(Page page, long id) {
      Query query;
      query = page.query(id);

      return href(query);
    }

    final String href(Query query) {
      final Page page;
      page = query.page;

      final String demo;
      demo = codec.encode(query);

      return page.href() + "&demo=" + demo;
    }

    final long nextReservation() {
      return reservation.next();
    }

    final <T1> void send(Note.Ref1<T1> note, T1 v1) {
      noteSink.send(note, v1);
    }

    final LocalDateTime today() {
      return LocalDateTime.now(clock);
    }

    final Html.Component transactional(Http.Exchange http, GET action) {
      return transactional.get(http, action);
    }

    final PostResult transactional(Http.Exchange http, POST action) {
      return transactional.post(http, action);
    }

    private Query decode(Http.Exchange http) {
      // We cannot rely on the path to render the different pages
      // of the application because this demo will be embedded in another page.
      // So, we use an URL query parameter.
      final String demo;
      demo = http.queryParam("demo");

      // the query parameter value is encoded/obfuscated.
      // we use the codec to decode it.
      final Query query;
      query = codec.decode(demo);

      http.set(Query.class, query);

      return query;
    }

  }

  //
  // SQL related classes
  //

  

  //
  // Embedded related classes
  //
  // Most Objectos Way applications will not require the classes in this section.
  // They are required because this demo will be embedded in another application.
  //

  /**
   * Represents an HTTP action in the demo application.
   */
  @FunctionalInterface interface Action<T> {

    T execute(Http.Exchange http);

  }

  /**
   * Handles a GET request. Similar to a {@code Http.Handler} instance, but for
   * an embedded application.
   */
  interface GET {

    Html.Component get(Http.Exchange http);

  }

  /**
   * Handles a POST request. Similar to a {@code Http.Handler} instance, but for
   * an embedded application.
   */
  interface POST {

    PostResult post(Http.Exchange http);

  }

  /**
   * Represents the demo query parameter.
   */
  record Query(Page page, long id, int aux) {

    final int idAsInt() {
      return (int) id;
    }

  }

}