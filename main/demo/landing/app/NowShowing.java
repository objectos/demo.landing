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
package demo.landing.app;

import java.util.List;
import objectos.way.Html;
import objectos.way.Http;
import objectos.way.Sql;

/**
 * The "Now Showing" controller.
 */
final class NowShowing implements Kino.GET {

  @Override
  public final Html.Component get(Http.Exchange http) {
    final Sql.Transaction trx;
    trx = http.get(Sql.Transaction.class);

    final List<NowShowingModel> items;
    items = NowShowingModel.query(trx);

    return Shell.create(shell -> {
      shell.app = new NowShowingView(items);

      shell.sources(
          Source.NowShowing,
          Source.NowShowingModel,
          Source.NowShowingView
      );
    });
  }

}