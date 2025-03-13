package demo.landing.app;

import demo.landing.LandingDemoConfig;
import demo.landing.StartTest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import objectos.way.App;
import objectos.way.Html;
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

  public static void handle(Http.Exchange http) {
    HANDLER.handle(http);
  }

  public static String writeResponseBody(Http.TestingExchange http) {
    Object body;
    body = http.responseBody();

    return switch (body) {
      case Html.Template html -> writeTemplate(html);

      default -> throw new UnsupportedOperationException("Unsupported type: " + body.getClass());
    };
  }

  private static String writeTemplate(Html.Template html) {
    Throwable t;
    t = new Throwable();

    StackTraceElement[] stackTrace;
    stackTrace = t.getStackTrace();

    StackTraceElement element;
    element = stackTrace[2];

    String simpleName;
    simpleName = element.getClassName();

    String methodName;
    methodName = element.getMethodName();

    String fileName;
    fileName = simpleName + "." + methodName + ".html";

    String tmpdir;
    tmpdir = System.getProperty("java.io.tmpdir");

    Path target;
    target = Path.of(tmpdir, fileName);

    try {
      byte[] bytes;
      bytes = html.mediaBytes();

      Files.write(target, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return html.toTestableText();
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