package com.railway;

import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;
import com.railway.utility.Utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final long DURATION = 10;   /*in  ms*/
    private static final long DELAY = 50;     /* in ms*/
    private static final int CHUNK_SIZE = 1000;
    private static final int maxThreadCapacity = 10000; /*Todo set a configuration */

    final private List<Map<String, Object>> bookingList = new ArrayList<>();
    final private Map<String, BookingInfo> trainsBooked = Collections.synchronizedMap(new HashMap<>());

    /*preparing parameters for batch query */
    final private List<List<Object>> reservationParams = new ArrayList<>();
    final private List<List<Object>> bookingStatusParams = new ArrayList<>();

    private void clear() {
        this.bookingList.clear();
        this.trainsBooked.clear();
        this.reservationParams.clear();
        this.bookingStatusParams.clear();
    }

    private boolean fetchFromQueue() throws Exception {

        String queueSql = """
                SELECT
                    booking_queue.id AS ticket_no,
                    passenger_details.train_no,
                    passenger_details.departure_date,
                    passenger_details.seat_class,
                    passenger_details.boarding_stoppage_no AS from_st,
                    passenger_details.deboarding_stoppage_no AS to_st,
                    passenger_details.u_email,
                    passenger_details.sl_no,
                    is_cancel,
                    passenger_details.seat_no
                FROM booking_queue
                INNER JOIN passenger_details
                ON booking_queue.id = passenger_details.booking_id
                WHERE (is_cancel = false OR cancel_request = true) AND reservation_status != 'CANCELLED'
                ORDER BY booking_queue.booking_time ASC
                LIMIT ?
                """;
        ResultSet resultSet = DButility.selectQuery(queueSql, List.of(CHUNK_SIZE));
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        boolean flag = false;
        while (resultSet.next()) {
            flag = true;
            int train_no = resultSet.getInt("train_no");
            Date date = resultSet.getDate("departure_date");
            String seat_class = resultSet.getString("seat_class");
            String key = train_no+"_"+date.toString()+"_"+seat_class;

            if(!trainsBooked.containsKey(key)) {
                trainsBooked.put(key, new BookingInfo());
            }
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            bookingList.add(row);
            bookingStatusParams.add(List.of(resultSet.getString("ticket_no")));
        }
        return  flag;
    }

    private void distributor() throws Exception {
        int threadsRequired = (int) Math.ceil(1.0 * bookingList.size() / maxThreadCapacity);
        System.out.println("threadsRequired = " + threadsRequired);
        List<Thread> threads = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < threadsRequired; i++) {
            List<Map<String, Object>> assigned = new ArrayList<>();
            System.out.println("for thread " + i + " assigning");
            while (j < bookingList.size() && assigned.size() < maxThreadCapacity) {
                System.out.println("booking list index = " + j);
                assigned.add(bookingList.get(j));
                j++;
            }
            if (assigned.isEmpty()) break;
            threads.add(new Thread(new ConformBooking(assigned, trainsBooked, reservationParams)));
            threads.get(i).start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
    private void putInDatabase() {
        try (Connection con = Connector.getConnection()) {
            con.setAutoCommit(false);
            String seatUpdateSql =
                            """
                            UPDATE seat_capacity
                            SET
                                available_seat = ?,
                                waiting = ?,
                                rac = ?,
                                seats = ? :: bit varying
                            WHERE train_no = ?
                                AND departure_date = ?
                                AND stoppage_no = ?
                                AND seat_class = ?
                            """;
            List<List<Object>> seatUpdateParams = new ArrayList<>();
            for (String key : trainsBooked.keySet()) {
                String[] parts = key.split("_");

                int train_no = Integer.parseInt(parts[0]);
                String departureDate = parts[1];
                String seatClass = parts[2];

                BookingInfo bookingInfo = trainsBooked.get(key);

                for (int i = 0; i < bookingInfo.size(); i++) {
                    List<Object> params = new ArrayList<>();

                    params.add(bookingInfo.getSeatStates().get(i).getAvailableSeats());
                    params.add(bookingInfo.getSeatStates().get(i).getWaiting());
                    params.add(bookingInfo.getSeatStates().get(i).getRac());
                    params.add(Utility.bitsetToString(bookingInfo.getSeatStates().get(i).getSeat()));
                    params.add(train_no);
                    params.add(Utility.toDate(departureDate));
                    params.add(i);
                    params.add(seatClass);

                    seatUpdateParams.add(params);
                }
            }
            int[] ints = DButility.batchQuery(con, seatUpdateSql, seatUpdateParams);
            System.out.println("Seat capacity Updated");


            String reservationSql = """
                    UPDATE passenger_details
                    SET
                       reservation_status = ?,
                       seat_no = ?,
                       booking_time = ?
                    WHERE
                        booking_id = ?
                        AND sl_no = ?;
                    """;
            if(reservationParams.size()>0) DButility.batchQuery(con, reservationSql, reservationParams);
            String queueClearSql =
                            """
                            DELETE FROM booking_queue
                            WHERE id = ?
                            """;
            int[] ints2 = DButility.batchQuery(con, queueClearSql, bookingStatusParams);
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Booking Server started");
                try {
                    server.clear();
                    boolean flag = server.fetchFromQueue();
                    if(flag){
                        System.out.println("Fetching from the queue is complete");
                        server.distributor();
                        System.out.println("Distributing among threads");
                        server.putInDatabase();
                        System.out.println("Putting data back to database complete");
                        System.out.println("Going to sleep");
                    }else{
                        System.out.println("Zero row fetched");
                    }
                    //Thread.sleep(1000000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, DELAY, TimeUnit.SECONDS.toMillis(DURATION));
    }
}
