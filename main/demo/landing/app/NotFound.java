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

/// Controller for any unmatched request to '/demo.landing/*'
final class NotFound implements Http.Handler {

  private final AppCtx ctx;

  NotFound(AppCtx ctx) {
    this.ctx = ctx;
  }

  @Override
  public final void handle(Http.Exchange http) {
    final AppReservation reservation;
    reservation = AppReservation.parse(http);

    final UiShell shell;
    shell = UiShell.of(opts -> {
      opts.homeAction = ctx.clickAction(AppView.HOME, reservation);

      opts.main = new NotFoundView(opts.homeAction);

      opts.sources = List.of(
          Source.NotFound,
          Source.NotFoundView
      );
    });

    http.notFound(shell);
  }

}