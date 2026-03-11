package demo.landing.app;

import demo.landing.StartTest;
import demo.landing.testing.TestingResponseListener;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;
import objectos.http.HttpExchange;
import objectos.way.App;
import objectos.way.Sql;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class Testing implements ISuiteListener {

  public static App.Injector INJECTOR;

  public static objectos.http.HttpHandler HANDLER;

  @Override
  public final void onStart(ISuite suite) {
    if (INJECTOR == null) {
      StartTest.boot();
    }
  }

  private static Sql.Transaction beginTrx() {
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

  public static String handle0(HttpExchange http) {
    HANDLER.handle(http);

    final TestingResponseListener listener;
    listener = http.get(TestingResponseListener.class);

    return listener.toString();
  }

  private static final Clock FIXED = Clock.fixed(
      LocalDateTime.of(2025, 4, 28, 13, 1).atZone(ZoneOffset.UTC).toInstant(),
      ZoneOffset.UTC
  );

  public static objectos.http.HttpExchange http(Consumer<? super HttpExchange.Options> more) {
    final TestingResponseListener listener;
    listener = new TestingResponseListener(4);

    return HttpExchange.create(options -> {
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

}