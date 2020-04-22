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

        p1.setOpponent(p2);
        p2.setOpponent(p1);

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

        // Can see how accurate it is by see if the wins are close to
        // the theoretical probability draws == 33% and Hands == 22%
        System.out.println
        (
        " Draws: " + p2.draws +
        " scissorsWin: " + p2.scissorsWin +
        " rockWin: " + p2.rockWin +
        " paperWin: " + p2.paperWin +
        " total: " + (p2.draws + p2.scissorsWin + p2.rockWin + p2.paperWin)
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
                paperWin = 0;

    private rpsTask opponent;
    CyclicBarrier barrier;
    Hands hand;

    rpsTask (int id, int rounds, CyclicBarrier barrier)
    {
        this.id = id;
        this.rounds = rounds;
        this.barrier = barrier;
    }

    public void setOpponent(rpsTask opponent)
    {
        this.opponent = opponent;
    }

    public Hands getHand() { return hand; }

    /**
    *@pream i the current round, and an instance of Hands to put the curr hand
    *
    *
    *@return gives the current thread a rand enum from Hands
    *
    *
    *@def This is how a thread choses at rand to use in the next round
    *
    */
    private void shoot(int i)
    {
            hand = Hands.rand();
            System.out.println
            (
            "Round " + i + ": " +
            "Player " + id + " selects " + hand
            );
    }

    /**
    *@pream hand is what the current thread used, i is the round
    *       requires an opponent thread to compete against
    *
    *@return prints if this thread won or lost and increments a counter for 
    *        what they use to win
    *
    *@def A switch statement that figures out who won or lost
    *     and what they used to win
    */
    private synchronized void winLoss(Hands hand, int i)
    {
        if( opponent.getHand() == hand )
        {
            draws++;
            System.out.println
            (
            "Round " + i + ": " +
            "Player " + id + " draw"
            );
        }
        else
        {
            switch(hand)
            {
                case Scissors:
                    if ( opponent.getHand() == Hands.Rock )
                    {
                        rockWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " loses"
                        );
                    }
                    else
                    {
                        scissorsWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " wins!"
                        );
                    }
                    break;
                case Rock:
                    if ( opponent.getHand() == Hands.Paper )
                    {
                        paperWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " loses"
                        );
                    }
                    else
                    {
                        rockWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " wins!"
                        );
                    }
                    break;
                case Paper:
                    if ( opponent.getHand() == Hands.Scissors )
                    {
                        scissorsWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " loses"
                        );
                    }
                    else
                    {
                        paperWin++;
                        System.out.println
                        (
                        "Round " + i + ": " +
                        "Player " + id + " wins!"
                        );
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public void run ()
    {
        for (int i = 0; i < rounds; i++)
        {
            try
            {
                shoot(i);
                barrier.await();
                winLoss(hand, i);
            } catch (InterruptedException | BrokenBarrierException e) {}
        }
    }
}
