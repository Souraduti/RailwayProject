package com.railway;

import com.railway.utility.Utility;

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
        try {
            while(!bookings.isEmpty()){
                bookings.removeIf(this::confirmation);
            }
            /*Seats allocated after ticket cancellation */
            bookingTrains.forEach((k,v)->{
                reservationParams.addAll(v.getParams());
            });
        } catch (Exception e) {
            reservationParams.clear();
            e.printStackTrace();
        }
    }

    private boolean confirmation(Map<String, Object> booking){
        int train_no = (int)booking.getOrDefault("train_no",null);
        Date departureDate = (Date) booking.getOrDefault("departure_date", new Date());
        String seatClass = (String) booking.get("seat_class");
        String key = train_no + "_" + Utility.dateToFormattedString(departureDate)+"_"+seatClass;
        BookingInfo bookingInfo;
        synchronized (bookingTrains) {
            bookingInfo = bookingTrains.getOrDefault(key, null);
            if(bookingInfo.getOwnerThreadId()!=Thread.currentThread().getId()&&bookingInfo.getOwnerThreadId()!=-1){
                System.out.println("Locked , Key = "+key);
                return  false;
            }
            bookingInfo.setOwnerThreadId(Thread.currentThread().getId());
        }
        try {
            if(!bookingInfo.isInitialized()){
                bookingInfo.initialize(train_no,departureDate,seatClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        boolean cancel = (boolean) booking.get("is_cancel");
        int boarding = (Integer)booking.getOrDefault("from_st",null);
        int deboarding = (Integer)booking.getOrDefault("to_st",null);
        int seatNumber;
        String seatStatus;
        if(cancel){
            seatNumber =  (Integer) booking.get("seat_no");
            bookingInfo.releaseSeat(boarding,deboarding,seatNumber);
            bookingInfo.fillCancelledSeats(train_no,departureDate,seatClass);
            seatNumber = -3;
            seatStatus = "CANCELLED";
        }else {
            boolean available = bookingInfo.isSeatAvailable(boarding, deboarding);
            seatNumber = bookingInfo.getSeatNumber(boarding, deboarding);
            if (!available) {
                seatNumber = -2;
                seatStatus = "WAITING LIST";
                bookingInfo.updateWaiting(boarding, deboarding, 1);
            } else if (seatNumber == -1) {
                seatStatus = "RAC";
                bookingInfo.updateRac(boarding, deboarding, 1);
                bookingInfo.updateAvailableSeat(boarding, deboarding, -1);
            } else {
                seatStatus = "CONFIRM";
                bookingInfo.updateAvailableSeat(boarding, deboarding, -1);
                bookingInfo.bookSeat(boarding, deboarding, seatNumber);
            }
        }
        List<Object> params  = new ArrayList<>();

        params.add(seatStatus);
        params.add(seatNumber);
        params.add(System.currentTimeMillis());
        params.add(booking.get("ticket_no"));
        params.add(booking.get("sl_no"));

        reservationParams.add(params);
        bookingInfo.setOwnerThreadId(-1);
        return  true;
    }
}
