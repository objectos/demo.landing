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

import demo.landing.LandingDemoConfig;
import module objectos.way;

/// Defines the application routes.
public final class Routes implements Http.Routing.Module {

  private final Transactional transactional;

  public Routes(LandingDemoConfig config) {
    transactional = Transactional.of(config.stage, config.database);
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.path("/demo.landing/{}", demo -> {
      // we filter all requests, even though NotFound does not require DB access.
      demo.filter(transactional, this::routes);

      demo.handler(new NotFound());
    });
  }

  private void routes(Http.RoutingPath routes) {

  }

}
