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
package demo.landing.testing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import objectos.way.Http;
import objectos.way.Media;
import objectos.way.Testable;

public class TestingResponseListener implements Http.ResponseListener {

  private static final char LF = '\n';

  private final StringBuilder out = new StringBuilder();

  private final Path target;

  public TestingResponseListener(int stackFrame) {
    final Throwable t;
    t = new Throwable();

    final StackTraceElement[] stackTrace;
    stackTrace = t.getStackTrace();

    final StackTraceElement element;
    element = stackTrace[stackFrame];

    final String simpleName;
    simpleName = element.getClassName();

    final String methodName;
    methodName = element.getMethodName();

    final String fileName;
    fileName = simpleName + "." + methodName + ".html";

    final String tmpdir;
    tmpdir = System.getProperty("java.io.tmpdir");

    target = Path.of(tmpdir, fileName);
  }

  @Override
  public final void status(Http.Version version, Http.Status status) {
    switch (version) {
      case HTTP_0_9 -> out.append("HTTP/0.9 ");

      case HTTP_1_0 -> out.append("HTTP/1.0 ");

      case HTTP_1_1 -> out.append("HTTP/1.1 ");
    }

    out.append(status.code());

    out.append(' ');

    out.append(status.reasonPhrase());

    out.append(LF);
  }

  @Override
  public final void header(Http.HeaderName name, String value) {
    out.append(name.headerCase());

    out.append(": ");

    out.append(value);

    out.append(LF);
  }

  private static final StandardOpenOption[] WRITE_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

  @Override
  public final void body(Object body) {
    out.append(LF);

    if (body instanceof Testable testable) {
      out.append(testable.toTestableText());
    }

    try {

      switch (body) {
        case byte[] bytes -> Files.write(target, bytes, WRITE_OPTIONS);

        case Media.Text text -> {
          try (Writer w = Files.newBufferedWriter(target, WRITE_OPTIONS)) {
            text.writeTo(w);
          }
        }

        case null, default -> {}
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  @Override
  public final String toString() {
    return out.toString();
  }

}