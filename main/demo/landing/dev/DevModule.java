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

import demo.landing.LandingDemo;
import demo.landing.LandingDemoConfig;
import demo.landing.www.Routes;
import java.nio.file.Path;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Web;

public final class DevModule implements Http.Routing.Module {

  private final App.Injector injector;

  private final Routes routes;

  public DevModule(App.Injector injector) {
    this.injector = injector;

    final LandingDemoConfig config;
    config = injector.getInstance(LandingDemoConfig.class);

    routes = new Routes(config);
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.install(routes);

    routing.path("/", path -> {
      path.allow(Http.Method.GET, http -> http.movedPermanently("/index.html"));
    });

    routing.path("/index.html", path -> {
      path.allow(Http.Method.GET, this::index);
    });

    routing.path("/ui/styles.css", path -> {
      path.allow(Http.Method.GET, this::styles);
    });

    final Web.Resources webResources;
    webResources = injector.getInstance(Web.Resources.class);

    routing.handler(webResources);
  }

  private void index(Http.Exchange http) {
    final Html.Component head;
    head = injector.getInstance(Html.Component.class);

    final DevView object;
    object = new DevView(head);

    http.ok(object);
  }

  private void styles(Http.Exchange http) {
    http.ok(Css.StyleSheet.create(opts -> {
      final Note.Sink noteSink;
      noteSink = injector.getInstance(Note.Sink.class);

      opts.noteSink(noteSink);

      opts.include(LandingDemo.styles());

      opts.scanDirectory(Path.of("work", "main"));
    }));
  }

}