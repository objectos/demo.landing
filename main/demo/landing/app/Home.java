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

import module java.base;
import module objectos.way;

/// The "/home" controller.
final class Home extends AppTransactional {

  Home(App.Injector injector) {
    super(injector);
  }

  @Override
  final void handle(Http.Exchange http, Sql.Transaction trx) {
    final AppUrl url;
    url = AppUrl.parse(http);

    final List<HomeModel> movies;
    movies = HomeModel.query(trx);

    final HomeView view;
    view = new HomeView(url, movies);

    http.ok(view);
  }

}