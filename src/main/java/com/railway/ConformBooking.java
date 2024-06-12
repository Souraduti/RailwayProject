package com.railway;

import com.railway.utility.DButility;
import com.railway.utility.Utility;

import java.sql.ResultSet;
import java.util.*;

public class ConformBooking implements Runnable {

    private final Map<String, BookingInfo> bookingTrains;
    private final List<Map<String, Object>> bookings;
    private final List<List<Object>> reservationParams;

    public ConformBooking(List<Map<String, Object>> bookings,Map<String, BookingInfo> bookingTrains,List<List<Object>> reservationParams) {
        this.bookings = bookings;
        this.bookingTrains = bookingTrains;
        this.reservationParams = reservationParams;
    }

    @Override
    public void run() {
        while(!bookings.isEmpty()){
            bookings.removeIf(this::confirmation);
            System.out.println("Bookings remaining : "+bookings.size());
            System.out.println("Thread ID : "+Thread.currentThread().getId());

        }
    }
    private List<Integer> getCapacity(int train_no,Date date) throws Exception{
        String sql = "SELECT available_capacity FROM seat_capacity WHERE train_no = ? AND departure_date = ?";
        List<Object> params = new ArrayList<>();
        params.add(train_no);
        params.add(date);
        ResultSet rs = DButility.selectQuery(sql,params);
        List<Integer> capacity = new ArrayList<>();
        while (rs.next()){
            capacity.add(rs.getInt(1));
        }
        return capacity;
    }

    private boolean confirmation(Map<String, Object> booking){

        int train_no = (int)booking.getOrDefault("train_no",null);
        Date departureDate = (Date) booking.getOrDefault("departure_date", new Date());
        String key = train_no + "_" + Utility.dateToFormattedString(departureDate);
        BookingInfo bookingInfo;
        synchronized (bookingTrains) {
            bookingInfo = bookingTrains.getOrDefault(key, null);
            /*if(bookingInfo==null){
                try {
                    bookingInfo = getCapacity(train_no,departureDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }*/
            if(bookingInfo.getOwnerThreadId()!=Thread.currentThread().getId()&&bookingInfo.getOwnerThreadId()!=-1){
                System.out.println("Locked");
                return  false;
            }
            bookingInfo.setOwnerThreadId(Thread.currentThread().getId());
        }
        try {
            if(!bookingInfo.hasCapacity()){
                bookingInfo.setCapacity(getCapacity(train_no,departureDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        int boarding = (Integer)booking.getOrDefault("boarding_stoppage_no",null);
        int deboarding = (Integer)booking.getOrDefault("deboarding_stoppage_no",null);
        int count = (Integer)booking.getOrDefault("t_count",null);
        boolean waiting  = bookingInfo.updateCapacity(boarding,deboarding,count);
        String reservationStatus = (waiting)?"Waiting List":"Confirmed";
        List<Object> params  = new ArrayList<>();
        params.add(booking.get("train_no"));
        params.add(booking.get("train_no"));
        params.add(booking.get("boarding_stoppage_no"));
        params.add(booking.get("train_no"));
        params.add(booking.get("deboarding_stoppage_no"));
        params.add(booking.get("t_count"));
        params.add(booking.get("departure_date"));
        params.add(booking.get("u_email"));
        params.add(reservationStatus);
        bookingInfo.setOwnerThreadId(-1);
        reservationParams.add(params);
        return  true;
    }
}
