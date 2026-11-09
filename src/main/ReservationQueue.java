
package main;

/**
 * Implements a singly linked queue to manage restaurant parties.
 * Supports adding, removing, and viewing parties in FIFO order.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/09/2026
 */

public class ReservationQueue
{
    //~ Private Inner Class ..................................................

    private static class Node
    {
        private Party data;
        private Node next;

        public Node(Party data)
        {
            this.data = data;
            this.next = null;
        }
    }


    //~ Fields ................................................................

    private Node front;
    private Node rear;
    private int size;


    //~ Constructors ..........................................................

    public ReservationQueue()
    {
        front = null;
        rear = null;
        size = 0;
    }


    //~ Public Methods ........................................................

    /**
     * Adds a party to the rear of the queue.
     *
     * @param party the party to add
     */
    public void enqueue(Party party)
    {
        if (party == null)
        {
            throw new IllegalArgumentException(
                "Party cannot be null.");
        }

        Node newNode = new Node(party);

        if (isEmpty())
        {
            front = newNode;
        }
        else
        {
            rear.next = newNode;
        }

        rear = newNode;
        size++;
    }


    /**
     * Removes and returns the first party.
     *
     * @return the first party, or null if empty
     */
    public Party dequeue()
    {
        if (isEmpty())
        {
            return null;
        }

        Party removed = front.data;
        front = front.next;
        size--;

        if (front == null)
        {
            rear = null;
        }

        return removed;
    }


    /**
     * Returns the first party without removing it.
     *
     * @return the first party, or null if empty
     */
    public Party peek()
    {
        if (isEmpty())
        {
            return null;
        }

        return front.data;
    }


    /**
     * Checks whether the queue is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return size == 0;
    }


    /**
     * Returns the number of waiting parties.
     *
     * @return queue size
     */
    public int size()
    {
        return size;
    }


    /**
     * Checks whether a matching party exists.
     *
     * @param party the party to find
     * @return true if found
     */
    public boolean contains(Party party)
    {
        if (party == null)
        {
            return false;
        }

        Node current = front;

        while (current != null)
        {
            if (current.data.equals(party))
            {
                return true;
            }

            current = current.next;
        }

        return false;
    }


    /**
     * Removes the first matching party from the queue.
     *
     * @param party the party to remove
     * @return true if removed
     */
    public boolean remove(Party party)
    {
        if (party == null || isEmpty())
        {
            return false;
        }

        if (front.data.equals(party))
        {
            dequeue();
            return true;
        }

        Node previous = front;
        Node current = front.next;

        while (current != null)
        {
            if (current.data.equals(party))
            {
                previous.next = current.next;

                if (current == rear)
                {
                    rear = previous;
                }

                size--;
                return true;
            }

            previous = current;
            current = current.next;
        }

        return false;
    }


    /**
     * Returns the parties in FIFO order.
     *
     * @return an array of waiting parties
     */
    public Party[] toArray()
    {
        Party[] parties = new Party[size];
        Node current = front;
        int index = 0;

        while (current != null)
        {
            parties[index] = current.data;
            current = current.next;
            index++;
        }

        return parties;
    }
}
}
