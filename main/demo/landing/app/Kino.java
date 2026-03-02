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
import objectos.script.Js;
import objectos.script.JsAction;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Sql;

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

    // update only the demo shell
    opts.update(SHELL);
  });

  public static final JsAction ONLOAD = Js.byId(SHELL).render("/demo.landing/home");

  private Kino() {
  }

  /**
   * Creates a new {@code Kino} instance with the specified configuration.
   */
  public static Kino create(LandingDemoConfig config) {
    throw new UnsupportedOperationException("Implement me");
  }

  public static Http.Routing.Module routes(LandingDemoConfig config) {
    return new AppRoutes(
        App.Injector.create(opts -> {
          opts.putInstance(Sql.Database.class, config.database);

          opts.putInstance(Clock.class, config.clock);

          final AppReservation reservation;
          reservation = new AppReservation(config.clock, config.reservationEpoch, config.reservationRandom);

          opts.putInstance(AppReservation.class, reservation);
        })
    );
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
    throw new UnsupportedOperationException("Implement me");
  }

  /**
   * Handles a POST request.
   */
  @Override
  public final Kino.PostResult post(Http.Exchange http) {
    throw new UnsupportedOperationException("Implement me");
  }

}