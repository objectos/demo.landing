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
package demo.landing.app;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/// The time of a particular movie screening.
record MovieShowtime(int showId, String time) {

  private MovieShowtime(Object[] row) {
    this(
        Integer.parseInt(row[0].toString()),
        row[1].toString()
    );
  }

  static List<MovieShowtime> of(Array array) throws SQLException {
    Object[] values;
    values = (Object[]) array.getArray();

    List<MovieShowtime> list;
    list = new ArrayList<>(values.length);

    for (Object value : values) {
      Array inner;
      inner = (Array) value;

      Object[] row;
      row = (Object[]) inner.getArray();

      MovieShowtime showtime;
      showtime = new MovieShowtime(row);

      list.add(showtime);
    }

    return List.copyOf(list);
  }

}