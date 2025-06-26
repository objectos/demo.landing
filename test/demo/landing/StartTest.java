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

import demo.landing.app.FixedClock;
import demo.landing.app.FixedGenerator;
import demo.landing.app.Testing;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HexFormat;
import objectos.way.App;
import objectos.way.Css;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Note;
import objectos.way.Script;
import objectos.way.Sql;
import org.h2.jdbcx.JdbcConnectionPool;
import org.testng.TestNG;

public final class StartTest extends Start {

  private final Path classOutput = Path.of("work", "main");

  private FixedGenerator fixedGenerator;

  public static void boot() {
    StartTest test;
    test = new StartTest();

    test.start(new String[] {});
  }

  public static void main(String[] args) {
    StartTest test;
    test = new StartTest();

    test.start(args);

    test.run();
  }

  private void run() {
    TestNG.main(new String[] {"-d", "work/test-output", "test/testng.xml"});
  }

  @Override
  final App.NoteSink noteSink() {
    return App.NoteSink.OfConsole.create(config -> {
      config.filter(note -> note.hasAny(Note.DEBUG, Note.INFO, Note.WARN, Note.ERROR));
    });
  }

  @Override
  final JdbcConnectionPool connectionPool() {
    // we create a in-memory database for testing
    final String url;
    url = "jdbc:h2:mem:testing;DB_CLOSE_DELAY=-1";

    return JdbcConnectionPool.create(url, "sa", "");
  }

  @Override
  final void dbMigrations(Sql.Migrations migrations) {
    // schema
    LandingDemoDb.migration01(migrations);

    // no data
  }

  @Override
  final LandingDemoConfig config(App.Injector injector) {
    fixedGenerator = new FixedGenerator(1L);

    final LocalDateTime startTime;
    startTime = Epoch.START;

    final ZonedDateTime epoch;
    epoch = startTime.atZone(ZoneOffset.systemDefault());

    final FixedClock clock;
    clock = new FixedClock(epoch);

    final HexFormat hexFormat;
    hexFormat = HexFormat.of();

    final byte[] codecKey;
    codecKey = hexFormat.parseHex("7b9e2a4f6c8d1e3b5a0f7d9c4e2b6a8f1d3c5e7b9a0f2d4c6e8b1a3f5c7d9e0b");

    final Sql.Database database;
    database = injector.getInstance(Sql.Database.class);

    final Note.Sink noteSink;
    noteSink = injector.getInstance(Note.Sink.class);

    return LandingDemoConfig.create(config -> {
      config.clock(clock);

      config.codecKey(codecKey);

      config.database(database);

      config.noteSink(noteSink);

      config.reservationEpoch(epoch.toInstant());

      config.reservationRandom(fixedGenerator);

      config.stage(LandingDemo.Stage.TESTING);
    });
  }

  @Override
  final Html.Component headComponent(App.Injector injector) {
    final Note.Sink noteSink;
    noteSink = injector.getInstance(Note.Sink.class);

    final Css.StyleSheet styleSheet;
    styleSheet = css(noteSink, classOutput);

    final String css;
    css = styleSheet.generate();

    final Script.Library library;
    library = Script.Library.of();

    final String script;
    script = library.toString();

    return html -> {
      html.style(css);

      html.script(script);
    };
  }

  @Override
  final App.Injector injector(App.Injector.Builder ctx) {
    ctx.putInstance(FixedGenerator.class, fixedGenerator);

    return ctx.build();
  }

  @Override
  final Http.Handler serverHandler(App.Injector injector) {
    Testing.INJECTOR = injector;

    final BootModule module;
    module = new BootModule(injector);

    final Http.Handler handler;
    handler = Http.Handler.of(module);

    Testing.HANDLER = handler;

    return handler;
  }

  @Override
  final int serverPort() {
    return TESTING_HTTP_PORT;
  }

  @Override
  final AutoCloseable server(Note.Sink noteSink, Http.Handler handler) {
    // noop autocloseable
    return () -> {};
  }

}