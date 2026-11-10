package tests;

import main.Party;
import main.ReservationQueue;
import student.TestCase;

/**
 * Tests the ReservationQueue class, the singly linked FIFO queue
 * that stores waiting parties. Verifies enqueue, dequeue, peek,
 * size tracking, middle removal, searching, and array conversion,
 * including empty-queue and null-input cases.
 *
 * @author Tyler Santiago (tylermsantiago)
 * @version 11/09/2026
 */
public class ReservationQueueTest extends TestCase
{
    //~ Fields ................................................................

    private ReservationQueue queue;
    private Party smith;
    private Party johnson;
    private Party henry;


    //~ Setup .................................................................

    /**
     * Creates an empty queue and three sample parties before each test.
     */
    @Override
    public void setUp()
    {
        queue = new ReservationQueue();
        smith = new Party("Smith", 4);
        johnson = new Party("Johnson", 2);
        henry = new Party("Henry", 6);
    }


    /**
     * Adds Smith, Johnson, and Henry to the queue in that order.
     */
    private void fillQueue()
    {
        queue.enqueue(smith);
        queue.enqueue(johnson);
        queue.enqueue(henry);
    }


    //~ Public Methods ........................................................

    /**
     * Tests that a new queue starts empty.
     */
    public void testNewQueueIsEmpty()
    {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertEquals(0, queue.toArray().length);
    }


    /**
     * Tests that enqueue adds a party and increases the size by one.
     */
    public void testEnqueue()
    {
        queue.enqueue(smith);

        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());
        assertEquals(smith, queue.peek());

        queue.enqueue(johnson);

        assertEquals(2, queue.size());
        assertEquals(smith, queue.peek());
    }


    /**
     * Tests that enqueue rejects a null party and leaves the
     * queue unchanged.
     */
    public void testEnqueueNull()
    {
        queue.enqueue(smith);

        try
        {
            queue.enqueue(null);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(1, queue.size());
        assertEquals(smith, queue.peek());
    }


    /**
     * Tests that dequeue removes parties in first-in, first-out order
     * and decreases the size each time.
     */
    public void testDequeueFifoOrder()
    {
        fillQueue();

        assertEquals(smith, queue.dequeue());
        assertEquals(2, queue.size());
        assertEquals(johnson, queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals(henry, queue.dequeue());
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }


    /**
     * Tests that dequeue returns null on an empty queue.
     */
    public void testDequeueEmpty()
    {
        assertNull(queue.dequeue());
        assertEquals(0, queue.size());
    }


    /**
     * Tests that the queue still works after being emptied,
     * which checks that rear is reset when the last party leaves.
     */
    public void testReuseAfterEmptying()
    {
        queue.enqueue(smith);
        queue.dequeue();

        queue.enqueue(johnson);
        queue.enqueue(henry);

        assertEquals(2, queue.size());
        assertEquals(johnson, queue.dequeue());
        assertEquals(henry, queue.dequeue());
        assertNull(queue.dequeue());
    }


    /**
     * Tests that peek returns the front party without removing it.
     */
    public void testPeek()
    {
        fillQueue();

        assertEquals(smith, queue.peek());
        assertEquals(smith, queue.peek());
        assertEquals(3, queue.size());
    }


    /**
     * Tests that peek returns null on an empty queue.
     */
    public void testPeekEmpty()
    {
        assertNull(queue.peek());
    }


    /**
     * Tests that isEmpty returns false when parties are waiting
     * and true when the queue is empty.
     */
    public void testIsEmpty()
    {
        assertTrue(queue.isEmpty());

        queue.enqueue(smith);
        assertFalse(queue.isEmpty());

        queue.dequeue();
        assertTrue(queue.isEmpty());
    }


    /**
     * Tests that size returns the correct count, including zero.
     */
    public void testSize()
    {
        assertEquals(0, queue.size());

        fillQueue();
        assertEquals(3, queue.size());
    }


    /**
     * Tests removing the front party.
     */
    public void testRemoveFront()
    {
        fillQueue();

        assertTrue(queue.remove(new Party("Smith", 4)));
        assertEquals(2, queue.size());
        assertEquals(johnson, queue.peek());
        assertFalse(queue.contains(smith));
    }


    /**
     * Tests removing a party from the middle of the queue
     * without changing the order of the remaining parties.
     */
    public void testRemoveMiddle()
    {
        fillQueue();

        assertTrue(queue.remove(new Party("Johnson", 2)));
        assertEquals(2, queue.size());

        Party[] parties = queue.toArray();
        assertEquals(2, parties.length);
        assertEquals(smith, parties[0]);
        assertEquals(henry, parties[1]);
    }


    /**
     * Tests removing the rear party, then enqueuing a new party
     * to confirm that rear was updated correctly.
     */
    public void testRemoveRear()
    {
        fillQueue();

        assertTrue(queue.remove(new Party("Henry", 6)));
        assertEquals(2, queue.size());

        Party patel = new Party("Patel", 3);
        queue.enqueue(patel);

        Party[] parties = queue.toArray();
        assertEquals(3, parties.length);
        assertEquals(smith, parties[0]);
        assertEquals(johnson, parties[1]);
        assertEquals(patel, parties[2]);
    }


    /**
     * Tests removing the only party, leaving the queue empty
     * and still usable.
     */
    public void testRemoveOnlyParty()
    {
        queue.enqueue(smith);

        assertTrue(queue.remove(smith));
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());

        queue.enqueue(johnson);
        assertEquals(johnson, queue.peek());
        assertEquals(1, queue.size());
    }


    /**
     * Tests that remove only takes out the first matching party
     * when duplicates are present.
     */
    public void testRemoveFirstMatchOnly()
    {
        queue.enqueue(smith);
        queue.enqueue(johnson);
        queue.enqueue(new Party("Smith", 4));

        assertTrue(queue.remove(smith));
        assertEquals(2, queue.size());
        assertEquals(johnson, queue.peek());
        assertTrue(queue.contains(smith));
    }


    /**
     * Tests that remove returns false for a party that is not
     * in the queue, a party with the same name but a different size,
     * a null party, and an empty queue.
     */
    public void testRemoveNotFound()
    {
        assertFalse(queue.remove(smith));

        fillQueue();

        assertFalse(queue.remove(new Party("Patel", 3)));
        assertFalse(queue.remove(new Party("Smith", 2)));
        assertFalse(queue.remove(null));
        assertEquals(3, queue.size());
    }


    /**
     * Tests that contains finds parties anywhere in the queue.
     */
    public void testContains()
    {
        fillQueue();

        assertTrue(queue.contains(new Party("Smith", 4)));
        assertTrue(queue.contains(new Party("Johnson", 2)));
        assertTrue(queue.contains(new Party("Henry", 6)));
        assertEquals(3, queue.size());
    }


    /**
     * Tests that contains returns false for a missing party,
     * a null party, and an empty queue.
     */
    public void testContainsNotFound()
    {
        assertFalse(queue.contains(smith));

        fillQueue();

        assertFalse(queue.contains(new Party("Patel", 3)));
        assertFalse(queue.contains(new Party("Henry", 5)));
        assertFalse(queue.contains(null));
    }


    /**
     * Tests that toArray returns all parties in FIFO order
     * without changing the queue.
     */
    public void testToArray()
    {
        fillQueue();

        Party[] parties = queue.toArray();

        assertEquals(3, parties.length);
        assertEquals(smith, parties[0]);
        assertEquals(johnson, parties[1]);
        assertEquals(henry, parties[2]);
        assertEquals(3, queue.size());
    }


    /**
     * Tests that toArray returns a new array each time, so changing
     * the returned array does not affect the queue.
     */
    public void testToArrayIsCopy()
    {
        fillQueue();

        Party[] parties = queue.toArray();
        parties[0] = null;

        assertEquals(smith, queue.peek());
        assertNotSame(parties, queue.toArray());
    }


    /**
     * Tests that toArray returns an empty array for an empty queue.
     */
    public void testToArrayEmpty()
    {
        Party[] parties = queue.toArray();

        assertNotNull(parties);
        assertEquals(0, parties.length);
    }
}