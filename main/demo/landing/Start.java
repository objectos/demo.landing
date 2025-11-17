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
import java.nio.file.Path;
import java.sql.SQLException;
import objectos.way.App;
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

    final App.Injector injector;
    injector = App.Injector.create(this::injector);

    final Note.Sink noteSink;
    noteSink = injector.getInstance(Note.Sink.class);

    final App.ShutdownHook shutdownHook;
    shutdownHook = injector.getInstance(App.ShutdownHook.class);

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

  void injector(App.Injector.Options ctx) {
    // Note.Sink
    final Note.Sink noteSink;
    noteSink = noteSink();

    ctx.putInstance(Note.Sink.class, noteSink);

    // bootstrap start event
    final Note.Ref0 startNote;
    startNote = Note.Ref0.create(getClass(), "S", Note.INFO);

    noteSink.send(startNote);

    // App.ShutdownHook
    final App.ShutdownHook shutdownHook;
    shutdownHook = App.ShutdownHook.create(opts -> opts.noteSink(noteSink));

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
  }

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

  void dbMigrations(Sql.Migrations migrations) {
    // schema
    LandingDemoDb.migration01(migrations);

    // data
    LandingDemoDb.migration02(migrations);
  }

  abstract App.NoteSink noteSink();

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

  private Web.Resources webResources(App.Injector injector) {
    try {
      return Web.Resources.create(opts -> {
        final Note.Sink noteSink;
        noteSink = injector.getInstance(Note.Sink.class);

        opts.noteSink(noteSink);

        opts.contentTypes("""
        .css: text/css; charset=utf-8
        .jpg: image/jpeg
        .js: text/javascript; charset=utf-8
        .woff2: font/woff2
        """);

        opts.addDirectory(Path.of("web-resources"));

        opts.addMedia("/ui/script.js", Script.Library.of());

        final Sql.Database db;
        db = injector.getInstance(Sql.Database.class);

        final Web.Resources.Library posters;
        posters = LandingDemoDb.posters(db);

        opts.include(posters);
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

  abstract Http.Handler serverHandler(App.Injector injector);

  abstract int serverPort();

  AutoCloseable server(Note.Sink noteSink, Http.Handler handler) {
    try {
      final Http.Server server;
      server = Http.Server.create(opts -> {
        opts.handler(handler);

        opts.bufferSize(1024, 4096);

        opts.noteSink(noteSink);

        opts.port(serverPort());
      });

      server.start();

      return server;
    } catch (IOException e) {
      throw App.serviceFailed("Http.Server", e);
    }
  }

}