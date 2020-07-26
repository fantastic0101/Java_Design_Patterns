package com.ashishtrivedi16.transactionscript;

import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Tests {@link Hotel}
 */
public class HotelTest {

  private static final String H2_DB_URL = "jdbc:h2:~/test";

  private Hotel hotel;
  private HotelDaoImpl dao;

  @BeforeEach
  public void setUp() throws Exception {
    final var dataSource = createDataSource();
    deleteSchema(dataSource);
    createSchema(dataSource);
    dao = new HotelDaoImpl(dataSource);
    addRooms(dao);
    hotel = new Hotel(dao);

  }

  @Test
  public void bookingRoomShouldChangeBookedStatusToTrue() throws Exception {
    hotel.bookRoom(1);
    assertTrue(dao.getById(1).get().isBooked());
  }

  @Test()
  public void bookingRoomWithInvalidIdShouldRaiseException() {
    assertThrows(Exception.class, () -> {
      hotel.bookRoom(999);
    });
  }

  @Test()
  public void bookingRoomAgainShouldRaiseException() {
    assertThrows(Exception.class, () -> {
      hotel.bookRoom(1);
      hotel.bookRoom(1);
    });
  }

  @Test
  public void NotBookingRoomShouldNotChangeBookedStatus() throws Exception {
    assertFalse(dao.getById(1).get().isBooked());
  }

  @Test
  public void cancelRoomBookingShouldChangeBookedStatus() throws Exception {
    hotel.bookRoom(1);
    assertTrue(dao.getById(1).get().isBooked());
    hotel.cancelRoomBooking(1);
    assertFalse(dao.getById(1).get().isBooked());
  }

  @Test
  public void cancelRoomBookingWithInvalidIdShouldRaiseException() {
    assertThrows(Exception.class, () -> {
      hotel.cancelRoomBooking(999);
    });
  }

  @Test
  public void cancelRoomBookingForUnbookedRoomShouldRaiseException() {
    assertThrows(Exception.class, () -> {
      hotel.cancelRoomBooking(1);
    });
  }


  private static void deleteSchema(DataSource dataSource) throws SQLException {
    try (var connection = dataSource.getConnection();
         var statement = connection.createStatement()) {
      statement.execute(RoomSchemaSql.DELETE_SCHEMA_SQL);
    }
  }

  private static void createSchema(DataSource dataSource) throws Exception {
    try (var connection = dataSource.getConnection();
         var statement = connection.createStatement()) {
      statement.execute(RoomSchemaSql.CREATE_SCHEMA_SQL);
    } catch (JdbcSQLException e) {
      throw new CustomException(e.getMessage(), e);
    }
  }

  public static DataSource createDataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setUrl(H2_DB_URL);
    return dataSource;
  }

  private static void addRooms(HotelDaoImpl hotelDao) throws Exception {
    for (var room : generateSampleRooms()) {
      hotelDao.add(room);
    }
  }

  public static List<Room> generateSampleRooms() {
    final var room1 = new Room(1, "Single", 50, false);
    final var room2 = new Room(2, "Double", 80, false);
    final var room3 = new Room(3, "Queen", 120, false);
    final var room4 = new Room(4, "King", 150, false);
    final var room5 = new Room(5, "Single", 50, false);
    final var room6 = new Room(6, "Double", 80, false);
    return List.of(room1, room2, room3, room4, room5, room6);
  }
}
