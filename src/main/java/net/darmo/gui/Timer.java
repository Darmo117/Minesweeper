package net.darmo.gui;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Timer extends Thread {
    private MainFrame frame;
    
    private boolean running;
    
    public Timer(MainFrame frame) {
        this.frame = frame;
    }
    
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        
        running = true;
        while (running) {
            GregorianCalendar cal = new GregorianCalendar();
            
            cal.setTimeInMillis(System.currentTimeMillis() - startTime);
            frame.updateTimer(cal.get(Calendar.HOUR_OF_DAY) - 1, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {}
        }
    }
    
    public void terminate() {
        running = false;
    }
}
