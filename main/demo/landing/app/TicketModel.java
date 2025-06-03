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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import objectos.way.Sql;

record TicketModel(
    long id,
    String purchaseTime,
    String title,
    String screen,
    String date,
    String time,
    double ammountPaid,
    List<Item> items
) {

  record Item(String seat, double price) {

    private Item(ResultSet rs, int idx) throws SQLException {
      this(
          rs.getString(idx++),
          rs.getDouble(idx++)
      );
    }

    private static List<Item> of(Array array) throws SQLException {
      Object[] values;
      values = (Object[]) array.getArray();

      List<Item> list;
      list = new ArrayList<>(values.length);

      for (Object value : values) {
        ResultSet rs;
        rs = (ResultSet) value;

        if (!rs.next()) {
          continue;
        }

        Item item;
        item = new Item(rs, 1);

        list.add(item);
      }

      return List.copyOf(list);
    }

  }

  private TicketModel(ResultSet rs, int idx) throws SQLException {
    this(
        rs.getLong(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getString(idx++),
        rs.getDouble(idx++),
        Item.of(rs.getArray(idx++))
    );
  }

  public static Optional<TicketModel> queryOptional(Sql.Transaction trx, long id) {
    trx.sql("""
    select
      RESERVATION.RESERVATION_ID,
      formatdatetime(RESERVATION.TICKET_TIME, 'EEE dd/LLL kk:mm'),
      MOVIE.TITLE,
      SCREEN.NAME,
      formatdatetime(SHOW.SHOWDATE, 'EEE dd/LLL'),
      formatdatetime(SHOW.SHOWTIME, 'kk:mm'),
      sum(SHOW.SEAT_PRICE),
      array_agg (
        row (concat(SEAT.SEAT_ROW, SEAT.SEAT_COL), SHOW.SEAT_PRICE)
      )
    from
      RESERVATION
      join SHOW on RESERVATION.SHOW_ID = SHOW.SHOW_ID
      join SCREENING on SHOW.SCREENING_ID = SCREENING.SCREENING_ID
      join MOVIE on SCREENING.MOVIE_ID = MOVIE.MOVIE_ID
      join SCREEN on SCREENING.SCREEN_ID = SCREEN.SCREEN_ID
      join SELECTION on RESERVATION.RESERVATION_ID = SELECTION.RESERVATION_ID
      join SEAT on SELECTION.SEAT_ID = SEAT.SEAT_ID
    where
      RESERVATION.RESERVATION_ID = ?
      and TICKET_TIME is not null
    group by
      RESERVATION.RESERVATION_ID
    """);

    trx.param(id);

    return trx.queryOptional(TicketModel::new);
  }

  final boolean singular() {
    return items.size() == 1;
  }

}