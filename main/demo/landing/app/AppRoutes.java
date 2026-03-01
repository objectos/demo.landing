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
import module objectos.way;

/// Declares the application routes.
public final class AppRoutes implements Http.Routing.Module {

  static final Html.Id ID = Html.Id.of("demo.landing");

  public static final JsAction ONLOAD = Js.byId(ID).render("/demo.landing/home");

  private final App.Injector injector;

  public AppRoutes(App.Injector injector) {
    this.injector = injector;
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.path("/demo.landing/home", GET, new Home(injector));
  }

}
