package com.railway;

import java.util.List;

public class BookingInfo {
    private long ownerThreadId;
    private List<Integer> capacity;

    public BookingInfo() {
        this.capacity = null;
        ownerThreadId = -1;
    }
    public BookingInfo(List<Integer> capacity) {
        this.capacity = capacity;
        ownerThreadId = -1;
    }

    public long getOwnerThreadId() {
        return ownerThreadId;
    }

    public void setOwnerThreadId(long ownerThreadId) {
        this.ownerThreadId = ownerThreadId;
    }

    public List<Integer> getCapacity() {
        return capacity;
    }
    public boolean updateCapacity(int boarding,int deboarding,int ticketCount){
        boolean flag = false;
        for(int i = boarding-1;i<=deboarding-1;i++){
            capacity.set(i,capacity.get(i)-ticketCount);
            if(capacity.get(i)<0){
                flag = true;
            }
        }
        return flag;
    }

    public void setCapacity(List<Integer> capacity) {
        this.capacity = capacity;
    }
    public boolean hasCapacity(){
        return capacity!=null;
    }
}
