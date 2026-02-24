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

import static objectos.way.Http.Method.GET;

import demo.landing.LandingDemoConfig;
import java.time.Clock;
import module objectos.way;

/// Declares the application routes.
public final class AppRoutes implements Http.Routing.Module {

  private final Clock clock;

  private final AppTransactional transactional;

  public AppRoutes(LandingDemoConfig config) {
    clock = config.clock;

    transactional = AppTransactional.of(config.stage, config.database);
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.path("/demo.landing/{}", demo -> {
      // we filter these requests with transactionl
      demo.filter(transactional, this::routes);

      demo.handler(Http.Handler.notFound());
    });
  }

  private void routes(Http.RoutingPath routes) {
    routes.subpath("home", GET, new Home());

    routes.subpath("movie/{id}", GET, new Movie(clock));
  }

}
