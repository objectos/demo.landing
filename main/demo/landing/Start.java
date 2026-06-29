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
package demo.landing;

import module objectos.way;
import objectos.http.Handler;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
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

    final Handler serverHandler;
    serverHandler = serverHandler(injector);

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

    // Application
    final LandingDemo demo;
    demo = demo(ctx);

    ctx.putInstance(LandingDemo.class, demo);

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
    final Path relative;
    relative = Path.of("work", "demo.landing");

    final Path path;
    path = relative.toAbsolutePath();

    final String url;
    url = "jdbc:h2:file:" + path;

    return JdbcConnectionPool.create(url, "sa", "");
  }

  abstract LandingDemo demo(App.Injector injector);

  Html.Component headComponent(App.Injector injector) {
    return html -> {
      html.link(html.rel("stylesheet"), html.type("text/css"), html.href("/ui/styles.css"));

      html.script(html.src("/ui/script.js"));
    };
  }

  abstract Handler serverHandler(App.Injector injector);

  abstract int serverPort();

  AutoCloseable server(Note.Sink noteSink, Handler handler) {
    try {
      return Server.create(opts -> {
        //opts.bufferSize(4096);

        opts.noteSink(noteSink);

        opts.port(serverPort());

        opts.host(host -> {
          host.handler(handler);

          host.staticFiles(files -> {
            final Path webResources;
            webResources = Path.of("web-resources");

            files.addDirectory(webResources);
          });
        });
      });
    } catch (IOException e) {
      throw App.serviceFailed("http.Server", e);
    }
  }

}