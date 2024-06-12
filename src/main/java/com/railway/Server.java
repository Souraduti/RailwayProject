package com.railway;

import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final long DURATION = 10;   /*in  ms*/
    private static final long DELAY = 100;     /* in ms*/
    private static final int CHUNK_SIZE = 3;
    private static final int maxThreadCapacity = 2; /*Todo set a configuration */

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

    private void fetchFromQueue() throws Exception {

        String queueSql = "SELECT * FROM booking_queue WHERE completion_status =? LIMIT ?;";
        ResultSet resultSet = DButility.selectQuery(queueSql, Arrays.asList("Pending", CHUNK_SIZE));
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            int train_no = resultSet.getInt("train_no");
            Date date = resultSet.getDate("departure_date");
            String key = train_no+"_"+date.toString();

            if(!trainsBooked.containsKey(key)){
                trainsBooked.put(key,new BookingInfo());
            }

            // removed columnCount from  hashmap constructor
            Map<String, Object> row = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            bookingList.add(row);
//            List<Object> bookingID = new ArrayList<>();
//            bookingID.add(resultSet.getString("id"));
            bookingStatusParams.add(List.of(resultSet.getString("id")));
        }
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
            //System.out.println("Size of booking parts :"+assigned.size());
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
            String seatUpdateSql = """
                     UPDATE seat_capacity
                     SET available_capacity = ?
                     WHERE train_no = ?
                     AND stoppage_no = ?
                     AND departure_date  = TO_DATE(?,'YYYY-MM-DD')
                    """;
            List<List<Object>> seatUpdateParams = new ArrayList<>();
            for (String key : trainsBooked.keySet()) {
                String[] parts = key.split("_");
                int train_no = Integer.parseInt(parts[0]);
                String departure_date = parts[1];
                List<Integer> capacityList = trainsBooked.get(key).getCapacity();
                for (int i = 0; i < capacityList.size(); i++) {
                    List<Object> params = new ArrayList<>();
                    params.add(capacityList.get(i));
                    params.add(train_no);
                    params.add(i + 1);
                    params.add(departure_date);
                    seatUpdateParams.add(params);
                }
            }
            int[] ints = DButility.batchQuery(con, seatUpdateSql, seatUpdateParams);
            System.out.println("Seat capacity Updated");
            for (int i = 0; i < ints.length; i++) {
                System.out.println("ints["+i+"] = " + ints[i]);
            }

            String reservationSql = """
                    INSERT INTO ticket_reservation (train_no,boarding,deboarding,t_count,departure_date,email,status)
                    VALUES(?,
                    (SELECT station_code FROM train_stoppage WHERE train_no = ? AND stoppage_no = ?),
                    (SELECT station_code FROM train_stoppage WHERE train_no = ? AND stoppage_no = ?),
                    ?,?,?,?)
                    """;
            int[] ints1 = DButility.batchQuery(con, reservationSql, reservationParams);
            System.out.println("Inserted into ticket Reservation");
            for (int i = 0; i < ints1.length; i++) {
                System.out.println("ints1["+i+"] = " + ints1[i]);
            }


            String bookingStatusSql = """
                    UPDATE booking_queue
                    SET completion_status = 'Processed'
                    WHERE id = ?
                    """;
            int[] ints2 = DButility.batchQuery(con, bookingStatusSql, bookingStatusParams);
            System.out.println("CHanging status in BookingQueue");
            for (int i = 0; i < ints2.length; i++) {
                System.out.println("ints2["+i+"] = " + ints2[i]);
            }
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
                    server.fetchFromQueue();
                    System.out.println("Fetching from the queue is complete");
                    server.distributor();
                    System.out.println("Distributing among threads");
                    server.putInDatabase();
                    System.out.println("Putting data back to database complete");
                    System.out.println("Going to sleep");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, DELAY, TimeUnit.SECONDS.toMillis(DURATION));
    }
}
