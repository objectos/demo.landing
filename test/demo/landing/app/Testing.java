package demo.landing.app;

import demo.landing.StartTest;
import java.util.function.Consumer;
import objectos.http.Handler;
import objectos.http.Request;
import objectos.http.Result;
import objectos.way.App;
import objectos.way.Sql;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class Testing implements ISuiteListener {

  public static App.Injector INJECTOR;

  public static Handler HANDLER;

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

  public static Result handle(Request req) {
    return HANDLER.handle(req);
  }

  public static void load(Sql.Transaction trx, String data) {
    trx.sql(Sql.SCRIPT, data);

    trx.batchUpdate();
  }

  public static String testable(Request req) {
    final Result result;
    result = HANDLER.handle(req);

    return result.toTestableText();
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