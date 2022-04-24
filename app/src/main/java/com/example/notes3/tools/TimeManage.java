package com.example.notes3.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by flyan on 6/19/18.
 */

public class TimeManage {

    public static String  getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date curDate  = new Date();
        return format.format(curDate);
    }

}
