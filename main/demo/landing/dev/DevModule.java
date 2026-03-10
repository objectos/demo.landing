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
package demo.landing.dev;

import static objectos.way.Http.Method.GET;

import demo.landing.LandingDemo;
import module java.base;
import module objectos.way;
import objectos.css.StyleSheet;

public final class DevModule implements Http.Routing.Module {

  private final LandingDemo ctx;

  private final Html.Component head;

  private final Note.Sink noteSink;

  private final Web.Resources webResources;

  public DevModule(App.Injector injector) {
    ctx = injector.getInstance(LandingDemo.class);

    head = injector.getInstance(Html.Component.class);

    noteSink = injector.getInstance(Note.Sink.class);

    webResources = injector.getInstance(Web.Resources.class);
  }

  @Override
  public final void configure(Http.Routing routing) {
    routing.install(ctx.localRoutes());

    routing.install(ctx.publicRoutes(webResources));

    routing.path("/", GET, http -> http.movedPermanently("/index.html"));

    routing.path("/index.html", GET, this::index);

    routing.path("/ui/styles.css", GET, this::styles);

    routing.handler(webResources);
  }

  private void index(Http.Exchange http) {
    final DevView object;
    object = new DevView(head);

    http.ok(object);
  }

  private void styles(Http.Exchange http) {
    http.ok(StyleSheet.create(opts -> {
      opts.noteSink(noteSink);

      opts.include(LandingDemo.styles());

      opts.scanDirectory(Path.of("work", "main"));
    }));
  }

}