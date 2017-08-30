import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    private CyclicBarrier barrier;
    private CountDownLatch cdFinish;
    private CountDownLatch cdStart;
    private int maxCars;

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CyclicBarrier barrier, CountDownLatch cdFinish,CountDownLatch cdStart, int maxCars) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.barrier = barrier;
        this.cdFinish = cdFinish;
        this.maxCars = maxCars;
        this.cdStart = cdStart;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cdStart.countDown();
        // Долго пытался синхронизировать начало объявления, что гонка началась
        // Объявление обычно появлялось после того как участники уже начали этап
        // После того как добавил sleep в 1 милисекунду - всё стало Ок
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
            if (i == race.getStages().size() - 1) {
                if (cdFinish.getCount() == maxCars) System.out.println(getName() + " WIN");
                cdFinish.countDown();
            }
        }
    }
}
