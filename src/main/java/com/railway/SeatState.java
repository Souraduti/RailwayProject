package com.railway;

import java.util.BitSet;

public class SeatState {
    private  int availableSeats;
    private int waiting;
    private int rac;
    BitSet seat;

    public SeatState() {

    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getWaiting() {
        return waiting;
    }

    public int getRac() {
        return rac;
    }

    public BitSet getSeat() {
        return seat;
    }

    public SeatState(int availableSeats, int waiting, int rac, BitSet seat) {
        this.availableSeats = availableSeats;
        this.waiting = waiting;
        this.rac = rac;
        this.seat = seat;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }

    public void setRac(int rac) {
        this.rac = rac;
    }

    public void setSeat(BitSet seat) {
        this.seat = seat;
    }
}
