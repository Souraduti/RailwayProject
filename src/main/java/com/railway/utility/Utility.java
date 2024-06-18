package com.railway.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;

public class Utility {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToFormattedString(Date date) {
        if (date == null) {
            return null;
        }
        return simpleDateFormat.format(date);
    }

    public static String longToFormattedString(long date) {
        if (date <= 0) {
            return null;
        }
        return simpleDateFormat.format(date);
    }
    public static Date toDate(String dateString){
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String bitsetToString(BitSet bitSet){
        StringBuilder sb = new StringBuilder();
        //System.out.println("bitSet Length = " + bitSet.length());
        for (int i =0;i<bitSet.length()-1;i++){
            if(bitSet.get(i)){
                sb.append(1);
            }else{
                sb.append(0);
            }
        }
        return sb.toString();
    }
    public static  BitSet stringToBitset(String bitString){
        BitSet bitSet = new BitSet(bitString.length()+1);
        bitSet.set(bitString.length(),true);
        for(int i = 0;i<bitString.length();i++){
            if(bitString.charAt(i)=='1'){
                bitSet.set(i,true);
            }else if(bitString.charAt(i)=='0'){
                bitSet.set(i,false);
            }else {
                return null;
            }
        }
        return bitSet;
    }

    private  static  final  int TICKET_NUMBER_LENGTH = 12;
    private static final Random r = new Random();
    /* this id is used as ticket number */
    public static String generateID(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<TICKET_NUMBER_LENGTH;i++){
            sb.append( r.nextInt(10));
        }
        return  sb.toString();
    }
}
