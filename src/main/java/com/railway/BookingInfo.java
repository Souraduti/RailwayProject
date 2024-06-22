package com.railway;

import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;
import com.railway.utility.Utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLOutput;
import java.util.*;

public class BookingInfo {
    private  boolean initialized;
    private long ownerThreadId;
    private List<SeatState> seatStates;
    private List<Map<String,Object>> waitingPassengers;
    private List<List<Object>> params;


    public List<List<Object>> getParams() {
        return params;
    }
    private int stoppageCount;

    public BookingInfo() {
        seatStates = null;
        waitingPassengers = null;
        initialized = false;
        stoppageCount = 0;
        ownerThreadId = -1;
        params = new ArrayList<>();
    }
    private void  fillWaitingPassengers(int trainID,Date dep_date,String seatClass) throws Exception{
        String sql = """
                    SELECT *
                    FROM passenger_details
                    WHERE train_no = ?
                    AND departure_date = ?
                    AND seat_class = ?
                    AND reservation_status IN ('WAITING LIST','RAC')
                    AND cancel_request = false
                    ORDER BY booking_time ASC
                    """;
        ResultSet resultSet = DButility.selectQuery(sql, Arrays.asList(trainID,dep_date,seatClass));
        waitingPassengers = DButility.getResultAsList(resultSet);
    }
    public boolean isInitialized() {
        return initialized;
    }

    public void initialize(int train_no, Date date,String seatClass) throws Exception {
        String sql = """
                SELECT available_seat,waiting,rac,seats
                FROM seat_capacity
                WHERE train_no = ? AND departure_date = ? AND seat_class = ?
                ORDER BY stoppage_no ASC""";
        List<Object> params = new ArrayList<>();
        params.add(train_no);
        params.add(date);
        params.add(seatClass);
        //System.out.println("seatClass = " + seatClass);
        ResultSet rs = DButility.selectQuery(sql,params);
        this.seatStates = new ArrayList<>();
        stoppageCount = 0;
        while (rs.next()){
            SeatState state = new SeatState();
            state.setAvailableSeats(rs.getInt("available_seat"));
            state.setWaiting(rs.getInt("waiting"));
            state.setRac(rs.getInt("rac"));
            String bitString = rs.getString("seats");
            BitSet bs = Utility.stringToBitset(bitString);
            state.setSeat(bs);
            seatStates.add(state);
            stoppageCount++;
        }
        this.initialized = true;
    }

    public long getOwnerThreadId() {
        return ownerThreadId;
    }

    public void setOwnerThreadId(long ownerThreadId) {
        this.ownerThreadId = ownerThreadId;
    }


    public int getSeatNumber(int boarding,int deboarding){
        BitSet temp = new BitSet();
        for(int i = boarding;i<deboarding;i++){
            temp.or(seatStates.get(i).getSeat());
        }
        for (int i = 0;i<temp.length();i++){
            if(!temp.get(i)) return i;
        }
        return  -1;
    }
    public  void bookSeat(int boarding,int deboarding,int seatNumber){
        for(int i = boarding;i<deboarding;i++){
            seatStates.get(i).getSeat().set(seatNumber);
        }
    }
    boolean isSeatAvailable(int boarding,int deboarding){
        for(int i = boarding;i<deboarding;i++){
            if(seatStates.get(i).getAvailableSeats()<=0) return false;
        }
        return true;
    }
    public void releaseSeat(int boarding,int deboarding,int seatNumber){
        if(seatNumber==-2){
            /*Waiting list*/
            updateWaiting(boarding,deboarding,-1);
            return;
        }
        if(seatNumber==-1){
            /* RAC */
            updateRac(boarding,deboarding,-1);
            updateAvailableSeat(boarding,deboarding,1);
            return;
        }
        /* confirmed Seat */
        updateAvailableSeat(boarding,deboarding,1);
        for(int i = boarding;i<deboarding;i++){
            seatStates.get(i).getSeat().set(seatNumber,false);
        }
    }
    public int size(){return stoppageCount;}

    public List<SeatState> getSeatStates() {
        return seatStates;
    }

    public int getStoppageCount() {
        return stoppageCount;
    }

    public void fillCancelledSeats(int trainID, Date dep_date,String seatClass)  {
        System.out.println("fill up invoked");
        if(waitingPassengers==null){
            try {
                fillWaitingPassengers(trainID,dep_date,seatClass);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        for(int i=0;i<waitingPassengers.size();i++) {
            Map<String,Object> row = waitingPassengers.get(i);
            if(row==null) continue;

            int from = (Integer) row.get("boarding_stoppage_no");
            int to = (Integer) row.get("deboarding_stoppage_no");

            if (!isSeatAvailable(from, to)) continue;

            List<Object> param = new ArrayList<>();
            String reservation_status;
            int seatNumber = getSeatNumber(from, to);
            boolean isRac = row.get("reservation_status").equals("RAC");

            /*This case implies RAC to RAC thus simply ignored*/
            if(seatNumber<0&&isRac) continue;

            if (seatNumber < 0) {
                /*Waiting List to RAC*/
                System.out.println("Waiting list to RAC");
                reservation_status = "RAC";
                updateAvailableSeat(from,to,-1);
                updateWaiting(from,to,-1);
                updateRac(from,to,1);
            } else if (isRac) {
                /*Rac to confirm*/
                System.out.println("Rac to Confirm");
                reservation_status = "CONFIRM";
                updateRac(from,to,-1);
                bookSeat(from, to, seatNumber);
            } else {
                /*Waiting list to confirm*/
                System.out.println("WaitingList to Confirm");
                reservation_status = "CONFIRM";
                updateWaiting(from,to,-1);
                updateAvailableSeat(from,to,-1);
                bookSeat(from, to, seatNumber);
            }
            param.add(reservation_status);
            param.add(seatNumber);
            param.add(row.get("booking_time"));
            param.add(row.get("booking_id"));
            param.add(row.get("sl_no"));
            waitingPassengers.set(i,null);
            params.add(param);
        }
    }

    public  void updateWaiting(int boarding,int deboarding,int value){
        for(int i = boarding;i<deboarding;i++){
            seatStates.get(i).setWaiting(seatStates.get(i).getWaiting()+value);
        }
    }
    public  void updateRac(int boarding,int deboarding,int value){
        for(int i = boarding;i<deboarding;i++){
            seatStates.get(i).setRac(seatStates.get(i).getRac()+value);
        }
    }
    public void updateAvailableSeat(int boarding,int deboarding,int value){
        for(int i = boarding;i<deboarding;i++){
            seatStates.get(i).setAvailableSeats(seatStates.get(i).getAvailableSeats()+value);
        }
    }
}
