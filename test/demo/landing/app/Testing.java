package demo.landing.app;

import demo.landing.LandingDemoConfig;
import demo.landing.StartTest;
import demo.landing.testing.TestingResponseListener;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;
import objectos.way.App;
import objectos.way.Http;
import objectos.way.Sql;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class Testing implements ISuiteListener {

  public static App.Injector INJECTOR;

  public static Http.Handler HANDLER;

  @Override
  public final void onStart(ISuite suite) {
    if (INJECTOR == null) {
      StartTest.boot();
    }
  }

  public static Sql.Transaction beginTrx() {
    final App.Injector injector;
    injector = Testing.INJECTOR;

    final Sql.Database db;
    db = injector.getInstance(Sql.Database.class);

    final Sql.Transaction trx;
    trx = db.beginTransaction(Sql.READ_COMMITED);

    trx.sql("set schema CINEMA");

    trx.update();

    return trx;
  }

  public static void commit(String data) {
    final Sql.Transaction trx;
    trx = beginTrx();

    try {
      trx.sql(Sql.SCRIPT, data);

      trx.batchUpdate();

      trx.commit();
    } finally {
      trx.close();
    }
  }

  public static Kino.Query decode(String hex) {
    return CodecHolder.INSTANCE.decode(hex);
  }

  public static String encode(Kino.Page page) {
    return encode(page, 0L);
  }

  public static String encode(Kino.Page page, long id) {
    return encode(page, id, 0);
  }

  public static String encode(Kino.Page page, long id, int aux) {
    final Kino.Query q;
    q = page.query(id, aux);

    return CodecHolder.INSTANCE.encode(q);
  }

  public static String handle0(Http.Exchange http) {
    HANDLER.handle(http);

    final TestingResponseListener listener;
    listener = http.get(TestingResponseListener.class);

    return listener.toString();
  }

  private static final Clock FIXED = Clock.fixed(
      LocalDateTime.of(2025, 4, 28, 13, 1).atZone(ZoneOffset.UTC).toInstant(),
      ZoneOffset.UTC
  );

  public static Http.Exchange http(Consumer<? super Http.Exchange.Options> more) {
    final TestingResponseListener listener;
    listener = new TestingResponseListener(4);

    return Http.Exchange.create(options -> {
      options.clock(FIXED);

      options.responseListener(listener);

      options.set(TestingResponseListener.class, listener);

      more.accept(options);
    });
  }

  public static void load(Sql.Transaction trx, String data) {
    trx.sql(Sql.SCRIPT, data);

    trx.batchUpdate();
  }

  public static void rollback(Consumer<? super Sql.Transaction> test) {
    final App.Injector injector;
    injector = Testing.INJECTOR;

    final Sql.Database db;
    db = injector.getInstance(Sql.Database.class);

    final Sql.Transaction trx;
    trx = db.beginTransaction(Sql.READ_COMMITED);

    trx.sql("set schema CINEMA");

    trx.update();

    try {
      test.accept(trx);
    } finally {
      Sql.rollbackAndClose(trx);
    }
  }

  private static final class CodecHolder {

    static KinoCodec INSTANCE = create();

    private static KinoCodec create() {
      final LandingDemoConfig config;
      config = INJECTOR.getInstance(LandingDemoConfig.class);

      return new KinoCodec(config.clock, config.codecKey());
    }

  }

}