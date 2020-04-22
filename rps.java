import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.*;

class rps
{
    public static void main (String[] args)
    {
        int rounds = 1000;
        CyclicBarrier barrier = new CyclicBarrier(2);
        rpsTask p1 = new rpsTask ( 1, rounds, barrier );
        rpsTask p2 = new rpsTask ( 2, rounds, barrier );

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);

        t1.start();
        t2.start();

        try
        {
            t1.join();
            t2.join();
        }
        catch (InterruptedException e) {}

        System.out.println
        (
        "Draws: " + p2.draws +
        " p1Win: " + p2.p1Win +
        " p2Win: " +  p2.p2Win +
        " scissorsWin: " + p2.scissorsWin +
        " rockWin: " + p2.rockWin +
        " paperWin: " + p2.paperWin
        );
    }
}

enum Hands
{
    Scissors,
    Rock,
    Paper;

    public static Hands rand()
    {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}


class rpsTask implements Runnable
{

    public int id,
                rounds,
                draws = 0,
                scissorsWin = 0,
                rockWin = 0,
                paperWin = 0,
                p1Win = 0,
                p2Win = 0;

    CyclicBarrier barrier;
    Hands hand;

    rpsTask (int id, int rounds, CyclicBarrier barrier)
    {
        this.id = id;
        this.rounds = rounds;
        this.barrier = barrier;
    }

    public Hands getHand() { return hand; }

    private void shoot(int i)
    {
            hand = Hands.rand();
            System.out.println
            (
            "Round " + i + ": " +
            "Player " + id + " selects " + hand
            );
    }

    private synchronized void winLoss()
    {
    }

    public void run ()
    {
        for (int i = 0; i < rounds; i++)
        {
            shoot(i);
            try
            {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {}
        }
    }
}
