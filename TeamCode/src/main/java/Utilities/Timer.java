package Utilities;

import java.util.ArrayList;

public class Timer
{
    private long startMills;
    private final ArrayList<String> intervalNames;
    private final ArrayList<Long> intervalTimes;

    public Timer()
    {
        intervalNames = new ArrayList<String>();
        intervalTimes = new ArrayList<Long>();
    }

    public void start()
    {
        startMills = System.currentTimeMillis();
        addInterval("Timer Start");

    }


    public long elapsedTime()
    {
        return System.currentTimeMillis() - startMills;
    }


    public void addInterval( String intervalName)
    {
        Long currMills = System.currentTimeMillis();
        addInterval(intervalName,currMills);
    }

    public String getTimerLog()
    {
        String log = "";
        for(int i = 0; i<intervalNames.size(); i++)
        {
            log = log.concat(intervalNames.get(i));
            log = log.concat("\t");
            log = log.concat(intervalTimes.get(i).toString());
            log = log.concat("\r\n");
        }
        return log;
    }

    private void addInterval(String intervalName, Long mills)
    {
        intervalNames.add(intervalName);
        intervalTimes.add(mills);
    }

}
