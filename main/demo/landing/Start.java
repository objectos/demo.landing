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
package demo.landing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Script;
import objectos.way.Sql;
import objectos.way.Web;
import org.h2.jdbcx.JdbcConnectionPool;

abstract class Start extends App.Bootstrap {

  public static final int DEVELOPMENT_HTTP_PORT = 8006;

  public static final int TESTING_HTTP_PORT = 9006;

  Start() {
  }

  @Override
  protected final void bootstrap() {
    final long startTime;
    startTime = System.currentTimeMillis();

    // Context
    final App.Injector.Builder ctx;
    ctx = App.Injector.Builder.create();

    // NoteSink
    final App.NoteSink noteSink;
    noteSink = noteSink();

    ctx.putInstance(Note.Sink.class, noteSink);

    // bootstrap start event
    final Note.Ref0 startNote;
    startNote = Note.Ref0.create(getClass(), "S", Note.INFO);

    noteSink.send(startNote);

    // App.ShutdownHook
    final App.ShutdownHook shutdownHook;
    shutdownHook = App.ShutdownHook.create(config -> config.noteSink(noteSink));

    shutdownHook.registerIfPossible(noteSink);

    ctx.putInstance(App.ShutdownHook.class, shutdownHook);

    // Sql.Database
    final Sql.Database db;
    db = db(ctx);

    ctx.putInstance(Sql.Database.class, db);

    // apply migrations
    db.migrate(this::dbMigrations);

    // Web.Resources
    final Web.Resources webResources;
    webResources = webResources(ctx);

    shutdownHook.register(webResources);

    ctx.putInstance(Web.Resources.class, webResources);

    // Application Config
    final LandingDemoConfig config;
    config = config(ctx);

    ctx.putInstance(LandingDemoConfig.class, config);

    // Head component
    final Html.Component headComponent;
    headComponent = headComponent(ctx);

    ctx.putInstance(Html.Component.class, headComponent);

    // Injector
    final App.Injector injector;
    injector = injector(ctx);

    // Http.Handler
    final Http.Handler serverHandler;
    serverHandler = serverHandler(injector);

    shutdownHook.registerIfPossible(serverHandler);

    // Http.Server
    final AutoCloseable httpServer;
    httpServer = server(noteSink, serverHandler);

    shutdownHook.register(httpServer);

    // bootstrap end event
    final Note.Long1 totalTimeNote;
    totalTimeNote = Note.Long1.create(getClass(), "T", Note.INFO);

    final long totalTime;
    totalTime = System.currentTimeMillis() - startTime;

    noteSink.send(totalTimeNote, totalTime);
  }

  void dbMigrations(Sql.Migrator migrator) {
    // schema
    LandingDemoDb.migration01(migrator);

    // data
    LandingDemoDb.migration02(migrator);
  }

  abstract App.NoteSink noteSink();

  private Sql.Database db(App.Injector injector) {
    try {
      final JdbcConnectionPool ds;
      ds = connectionPool();

      ds.setMaxConnections(4);

      final App.ShutdownHook shutdownHook;
      shutdownHook = injector.getInstance(App.ShutdownHook.class);

      shutdownHook.register(ds::dispose);

      return Sql.Database.create(config -> {
        config.dataSource(ds);

        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        config.noteSink(noteSink);
      });
    } catch (SQLException e) {
      throw App.serviceFailed("Sql.Database", e);
    }
  }

  JdbcConnectionPool connectionPool() throws SQLException {
    Path relative;
    relative = Path.of("work", "demo.landing");

    Path path;
    path = relative.toAbsolutePath();

    String url;
    url = "jdbc:h2:file:" + path;

    return JdbcConnectionPool.create(url, "sa", "");
  }

  abstract LandingDemoConfig config(App.Injector injector);

  final Css.StyleSheet css(Note.Sink noteSink, Path directory) {
    return Css.StyleSheet.create(config -> {
      config.noteSink(noteSink);

      config.scanDirectory(directory);

      config.theme("""
      --font-sans: 'InterVariable', var(--default-font-sans);
      --font-mono: 'Hack', var(--default-font-mono);
      --color-body: var(--color-white);
      --color-border: var(--color-gray-200);
      --color-btn-ghost: var(--color-body);
      --color-btn-ghost-active: color-mix(in oklab, var(--color-btn-ghost) 85%, black 15%);
      --color-btn-ghost-hover: color-mix(in oklab, var(--color-btn-ghost) 90%, black 10%);
      --color-btn-ghost-text: var(--color-text);
      --color-btn-primary: var(--color-blue-600);
      --color-btn-primary-active: color-mix(in oklab, var(--color-btn-primary) 70%, black 30%);
      --color-btn-primary-hover: color-mix(in oklab, var(--color-btn-primary) 85%, black 15%);
      --color-btn-primary-text: var(--color-gray-50);
      --color-focus: var(--color-blue-600);
      --color-footer: var(--color-gray-700);
      --color-footer-text: var(--color-gray-100);
      --color-high-comment: var(--color-gray-500);
      --color-high-keyword: var(--color-blue-700);
      --color-high-literal: var(--color-red-600);
      --color-high-meta: var(--color-yellow-600);
      --color-high-string: var(--color-green-700);
      --color-html: var(--color-gray-50);
      --color-icon: var(--color-gray-800);
      --color-layer: var(--color-stone-100);
      --color-link: var(--color-blue-600);
      --color-link-hover: color-mix(in oklab, var(--color-link) 85%, black 15%);
      --color-logo: var(--color-gray-800);
      --color-logo-hover: var(--color-link);
      --color-text: var(--color-gray-800);
      --color-text-secondary: var(--color-gray-600);
      """);

      config.theme("@media (prefers-color-scheme: dark)", """
      --color-body: var(--color-neutral-800);
      --color-border: var(--color-neutral-600);
      --color-btn-ghost-active: color-mix(in oklab, var(--color-btn-ghost) 85%, white 15%);
      --color-btn-ghost-hover: color-mix(in oklab, var(--color-btn-ghost) 90%, white 10%);
      --color-focus: var(--color-white);
      --color-high-comment: var(--color-fuchsia-400);
      --color-high-keyword: var(--color-blue-400);
      --color-high-literal: var(--color-red-400);
      --color-high-meta: var(--color-pink-400);
      --color-high-string: var(--color-green-300);
      --color-icon: var(--color-gray-200);
      --color-layer: var(--color-stone-900);
      --color-link: var(--color-blue-400);
      --color-link-hover: color-mix(in oklab, var(--color-link) 85%, white 15%);
      --color-logo: var(--color-neutral-100);
      --color-text: var(--color-neutral-100);
      --color-text-secondary: var(--color-neutral-300);
      """);
    });
  }

  private Web.Resources webResources(App.Injector injector) {
    try {
      return Web.Resources.create(config -> {
        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        config.noteSink(noteSink);

        config.contentTypes("""
        .css: text/css; charset=utf-8
        .jpg: image/jpeg
        .js: text/javascript; charset=utf-8
        .woff2: font/woff2
        """);

        config.addDirectory(Path.of("web-resources"));

        config.addTextFile("/ui/script.js", Script.getSource(), StandardCharsets.UTF_8);

        final Sql.Database db;
        db = injector.getInstance(Sql.Database.class);

        LandingDemoDb.createPosters(db, config);
      });
    } catch (IOException e) {
      throw App.serviceFailed("Web.Resources", e);
    }
  }

  Html.Component headComponent(App.Injector injector) {
    return html -> {
      html.link(html.rel("stylesheet"), html.type("text/css"), html.href("/ui/styles.css"));

      html.script(html.src("/ui/script.js"));
    };
  }

  abstract App.Injector injector(App.Injector.Builder ctx);

  abstract Http.Handler serverHandler(App.Injector injector);

  abstract int serverPort();

  AutoCloseable server(Note.Sink noteSink, Http.Handler handler) {
    try {
      final Http.Server server;
      server = Http.Server.create(config -> {
        config.handler(handler);

        config.bufferSize(1024, 4096);

        config.noteSink(noteSink);

        config.port(serverPort());
      });

      server.start();

      return server;
    } catch (IOException e) {
      throw App.serviceFailed("Http.Server", e);
    }
  }

}