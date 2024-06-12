package com.railway;

import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
* Has to implement map so that we can check seats are actually available or not without querying database
* to notify user if their booking is successful, or they are in waiting list
* have to inform seat number for booked seat
* update ticket reservation table
* */
public class SeatAllocator {
    /*
    Number of Rows to be fetched in one go from queue
    */

    private static  final long DURATION = 10;   /*in  ms*/
    private static final long DELAY  = 100;     /*in  ms*/
    private Map<Integer,List<Integer>> available;
    public static void main(String[] args){


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try (Connection con = Connector.getConnection()){
                    System.out.println(System.currentTimeMillis());
                    String queueSql = "SELECT * FROM booking_queue WHERE completion_status ='Pending' LIMIT ?;";
                    List<Object> chunkSize = new ArrayList<>();
                    //chunkSize.add(CHUNKSIZE);
                    String allocationSql = """
                            UPDATE seat_capacity
                            SET available_capacity = available_capacity - ?
                            WHERE train_no = ?
                            AND stopage_no BETWEEN ? AND ?
                            AND departure_date = ?;
                            """;
                    String statusSql= """
                            UPDATE booking_queue
                            SET completion_status = 'Processed'
                            WHERE  id = ?; 
                            """;
                    List<List<Object>> params1 = new ArrayList<>();
                    List<List<Object>> params2 = new ArrayList<>();
                    ResultSet resultSet = DButility.selectQuery(con,queueSql,chunkSize);
                    Thread t =new Thread(new Runnable(){

                        @Override
                        public void run() {
                            System.out.println("hi");
                        }
                    });
                    /*t.start();
                    t.interrupt();
                    notifyAll();*/
                    while (resultSet.next()) {
                        List<Object> values = new ArrayList<>();
                        values.add(resultSet.getInt("t_count"));
                        values.add(resultSet.getInt("train_no"));
                        values.add(resultSet.getInt("boarding_stopage_no"));
                        values.add(resultSet.getInt("deboarding_stopage_no"));
                        values.add(resultSet.getDate("departure_date"));
                        params1.add(values);

                        List<Object> id = new ArrayList<>();
                        id.add(resultSet.getString("id"));
                        params2.add(id);
                    }

                    DButility.otherQuery("begin");
                    DButility.batchQuery(con,allocationSql, params1);
                    DButility.batchQuery(con,statusSql,params2);
                    DButility.otherQuery("commit");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, DELAY, TimeUnit.SECONDS.toMillis(DURATION));
    }
}
