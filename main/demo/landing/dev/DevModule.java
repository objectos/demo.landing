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
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Web;

public final class DevModule extends Http.Module {

  private final App.Injector injector;

  private final Kino demo;

  public DevModule(App.Injector injector) {
    this.injector = injector;

    final LandingDemoConfig config;
    config = injector.getInstance(LandingDemoConfig.class);

    demo = Kino.create(config);
  }

  @Override
  protected final void configure() {
    route("/",
        movedPermanently("/index.html"));

    route("/index.html",
        handler(this::index));

    route("/demo/landing",
        handler(this::endpoint));

    route("/ui/styles.css",
        handlerFactory(DevStyles::new, injector));

    final Web.Resources webResources;
    webResources = injector.getInstance(Web.Resources.class);

    route("/ui/*",
        handler(webResources));

    route("/demo/landing/poster1.jpg", handler(webResources));
    route("/demo/landing/poster2.jpg", handler(webResources));
    route("/demo/landing/poster3.jpg", handler(webResources));
    route("/demo/landing/poster4.jpg", handler(webResources));
  }

  private void index(Http.Exchange http) {
    switch (http.method()) {
      case GET, HEAD -> respond(http, Http.Status.OK, demo.get(http));

      default -> http.methodNotAllowed();
    }
  }

  private void endpoint(Http.Exchange http) {
    switch (http.method()) {
      case POST -> {

        final Kino.PostResult result;
        result = demo.post(http);

        switch (result) {
          case Kino.Embed embed -> respond(http, embed.status(), embed.get());

          case Kino.Redirect redirect -> http.found(redirect.get());
        }

      }

      default -> http.methodNotAllowed();
    }
  }

  private void respond(Http.Exchange http, Http.Status status, Html.Component view) {
    final Html.Component head;
    head = injector.getInstance(Html.Component.class);

    final DevView object;
    object = new DevView(head, view);

    http.respond(status, object);
  }

}