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
package demo.landing.dev;

import demo.landing.LandingDemoConfig;
import demo.landing.app.Kino;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Media;
import objectos.way.Web;

public final class DevModule implements Http.Routing.Module {

  private final App.Injector injector;

  private final Kino demo;

  private final Web.Resources webResources;

  public DevModule(App.Injector injector) {
    this.injector = injector;

    final LandingDemoConfig config;
    config = injector.getInstance(LandingDemoConfig.class);

    demo = Kino.create(config);

    webResources = injector.getInstance(Web.Resources.class);
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.path("/", path -> {
      path.allow(Http.Method.GET, http -> http.movedPermanently("/index.html"));
    });

    routing.path("/index.html", path -> {
      path.allow(Http.Method.GET, this::index);
    });

    routing.path("/demo/landing", path -> {
      path.allow(Http.Method.POST, this::endpoint);
    });

    routing.path("/demo/landing/poster1.jpg", webResources::handlePath);
    routing.path("/demo/landing/poster2.jpg", webResources::handlePath);
    routing.path("/demo/landing/poster3.jpg", webResources::handlePath);
    routing.path("/demo/landing/poster4.jpg", webResources::handlePath);

    routing.path("/ui/styles.css", path -> {
      path.allow(Http.Method.GET, this::styles);
    });

    routing.path("/ui/{}", webResources::handlePath);
  }

  private void index(Http.Exchange http) {
    respond(http, Http.Status.OK, demo.get(http));
  }

  private void endpoint(Http.Exchange http) {
    final Kino.PostResult result;
    result = demo.post(http);

    switch (result) {
      case Kino.Embed embed -> respond(http, embed.status(), embed.get());

      case Kino.Redirect redirect -> {
        final String location;
        location = redirect.get();

        http.found(location);
      }
    }
  }

  private void styles(Http.Exchange http) {
    final Media.Text css;
    css = injector.getInstance(Css.StyleSheet.class);

    http.ok(css);
  }

  private void respond(Http.Exchange http, Http.Status status, Html.Component view) {
    final Html.Component head;
    head = injector.getInstance(Html.Component.class);

    final DevView object;
    object = new DevView(head, view);

    switch (status.code()) {
      case 200 -> http.ok(object);
      case 400 -> http.badRequest(object);
      default -> throw new AssertionError("Unexpected status=" + status);
    }
  }

}