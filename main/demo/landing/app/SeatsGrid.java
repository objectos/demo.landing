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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import objectos.way.Sql;

final class SeatsGrid implements Iterable<SeatsGrid.Cell> {

  record Cell(
      int gridY,
      int gridX,
      int seatId,
      String name,
      int state
  ) {

    private Cell(ResultSet rs, int idx) throws SQLException {
      this(
          rs.getInt(idx++),
          rs.getInt(idx++),
          rs.getInt(idx++),
          rs.getString(idx++),
          rs.getInt(idx++)
      );
    }

    public final boolean checked() {
      return state == 1;
    }

    public final boolean reserved() {
      return state == 2;
    }

  }

  private final List<Cell> cells;

  private SeatsGrid(List<Cell> cells) {
    this.cells = cells;
  }

  public static SeatsGrid query(Sql.Transaction trx, long reservationId) {
    trx.sql("""
    with
      MAIN as (
        select
          RESERVATION.RESERVATION_ID,
          SHOW.SHOW_ID,
          SCREENING.SCREEN_ID
        from
          RESERVATION
          join SHOW on RESERVATION.SHOW_ID = SHOW.SHOW_ID
          join SCREENING on SHOW.SCREENING_ID = SCREENING.SCREENING_ID
        where
          RESERVATION.RESERVATION_ID = ?
      ),
      GRID as (
        select
          X as GRID_Y,
          gx.GRID_X
        from
          system_range (0, 9)
          cross join (
            select
              X as GRID_X
            from
              system_range (0, 9)
          ) gx
      ),
      SELF as (
        select
          SEAT_ID
        from
          SELECTION
        where
          RESERVATION_ID = ( select RESERVATION_ID from MAIN )
      ),
      OTHERS as (
        select
          SELECTION.SEAT_ID
        from
          MAIN
          join RESERVATION on MAIN.RESERVATION_ID <> RESERVATION.RESERVATION_ID
          and MAIN.SHOW_ID = RESERVATION.SHOW_ID
          join SELECTION on RESERVATION.RESERVATION_ID = SELECTION.RESERVATION_ID
      )
    select
      GRID.GRID_Y,
      GRID.GRID_X,
      coalesce(SEAT.SEAT_ID, -1) as SEAT_ID,
      concat (SEAT_ROW, SEAT_COL) as SEAT_NAME,
      case SELF.SEAT_ID
        when is not null then 1
        else case OTHERS.SEAT_ID
          when is not null then 2
          else 0
        end
      end
    from
      MAIN
      cross join GRID

      left join SEAT on MAIN.SCREEN_ID = SEAT.SCREEN_ID
      and GRID.GRID_Y = SEAT.GRID_Y
      and GRID.GRID_X = SEAT.GRID_X

      left join SELF
      on SEAT.SEAT_ID = SELF.SEAT_ID

      left join OTHERS
      on SEAT.SEAT_ID = OTHERS.SEAT_ID
    order by
      GRID.GRID_Y,
      GRID.GRID_X
    """);

    trx.param(reservationId);

    final List<Cell> cells;
    cells = trx.query(Cell::new);

    return new SeatsGrid(cells);
  }

  @Override
  public final Iterator<Cell> iterator() {
    return cells.iterator();
  }

  @Override
  public final String toString() {
    final StringBuilder sb;
    sb = new StringBuilder();

    sb.append(". = empty space\n");
    sb.append("# = reserved\n");
    sb.append("o = selectable\n");
    sb.append("x = checked\n");

    int lastY = -1;

    for (SeatsGrid.Cell cell : cells) {
      int gridY;
      gridY = cell.gridY;

      if (gridY != lastY) {
        sb.append('\n');

        lastY = gridY;
      } else {
        sb.append(' ');
      }

      int seatId;
      seatId = cell.seatId;

      if (seatId < 0) {
        sb.append('.');
      } else if (cell.checked()) {
        sb.append('x');
      } else if (cell.reserved()) {
        sb.append('#');
      } else {
        sb.append('o');
      }
    }

    sb.append('\n');

    return sb.toString();
  }

}