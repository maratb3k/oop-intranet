package project;

public class ScheduledTask extends Thread {

    public ScheduledTask () {}

    @Override
    public void run() {
        while (true) {
            Intranet.INSTANCE.dateUpdate();
            try {
                Thread.sleep(20 * 60 * 60 * 1000);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }
    }
}
