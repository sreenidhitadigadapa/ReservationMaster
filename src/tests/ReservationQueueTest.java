
package tests;

import main.Party;
import main.ReservationQueue;
import student.TestCase;

/**
 * Tests the linked queue implementation.
 */

public class ReservationQueueTest extends TestCase
{
    private ReservationQueue queue;
    private Party first;
    private Party second;
    private Party third;


    public void setUp()
    {
        queue = new ReservationQueue();

        first = new Party("Smith", 4);
        second = new Party("Johnson", 2);
        third = new Party("Patel", 3);
    }


    public void testEmptyQueue()
    {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.dequeue());
        assertEquals(0, queue.toArray().length);
    }


    public void testEnqueue()
    {
        queue.enqueue(first);

        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());
        assertEquals(first, queue.peek());
    }


    public void testEnqueueNull()
    {
        try
        {
            queue.enqueue(null);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertTrue(queue.isEmpty());
    }


    public void testDequeueFIFO()
    {
        queue.enqueue(first);
        queue.enqueue(second);
        queue.enqueue(third);

        assertEquals(first, queue.dequeue());
        assertEquals(second, queue.dequeue());
        assertEquals(third, queue.dequeue());

        assertTrue(queue.isEmpty());
        assertNull(queue.dequeue());
    }


    public void testPeekDoesNotRemove()
    {
        queue.enqueue(first);

        assertEquals(first, queue.peek());
        assertEquals(first, queue.peek());
        assertEquals(1, queue.size());
    }


    public void testRemoveFront()
    {
        queue.enqueue(first);
        queue.enqueue(second);

        assertTrue(queue.remove(first));

        assertEquals(second, queue.peek());
        assertEquals(1, queue.size());
    }


    public void testRemoveMiddle()
    {
        queue.enqueue(first);
        queue.enqueue(second);
        queue.enqueue(third);

        assertTrue(queue.remove(second));

        assertEquals(2, queue.size());
        assertEquals(first, queue.dequeue());
        assertEquals(third, queue.dequeue());
    }


    public void testRemoveRear()
    {
        queue.enqueue(first);
        queue.enqueue(second);
        queue.enqueue(third);

        assertTrue(queue.remove(third));
        assertEquals(2, queue.size());

        queue.enqueue(new Party("Jones", 5));

        assertEquals(first, queue.dequeue());
        assertEquals(second, queue.dequeue());
        assertEquals(new Party("Jones", 5), queue.dequeue());
    }


    public void testRemoveNotFound()
    {
        queue.enqueue(first);

        assertFalse(queue.remove(second));
        assertFalse(queue.remove(null));
        assertEquals(1, queue.size());
    }


    public void testRemoveFromEmptyQueue()
    {
        assertFalse(queue.remove(first));
        assertTrue(queue.isEmpty());
    }


    public void testContains()
    {
        queue.enqueue(first);
        queue.enqueue(second);

        assertTrue(queue.contains(first));
        assertTrue(queue.contains(second));
        assertFalse(queue.contains(third));
        assertFalse(queue.contains(null));
    }


    public void testToArray()
    {
        queue.enqueue(first);
        queue.enqueue(second);

        Party[] parties = queue.toArray();

        assertEquals(2, parties.length);
        assertEquals(first, parties[0]);
        assertEquals(second, parties[1]);

        parties[0] = third;

        assertEquals(first, queue.peek());
    }


    public void testRemoveFirstDuplicate()
    {
        queue.enqueue(first);
        queue.enqueue(second);
        queue.enqueue(new Party("Smith", 4));

        assertTrue(queue.remove(first));

        Party[] remaining = queue.toArray();

        assertEquals(2, remaining.length);
        assertEquals(second, remaining[0]);
        assertEquals(first, remaining[1]);
    }


    public void testSingleElementQueue()
    {
        queue.enqueue(first);

        assertEquals(first, queue.dequeue());
        assertTrue(queue.isEmpty());

        queue.enqueue(second);

        assertEquals(second, queue.peek());
        assertEquals(1, queue.size());
    }
}
