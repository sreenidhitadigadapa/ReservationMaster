
package main;

/**
 * Implements a singly linked queue to manage restaurant parties.
 * Supports adding, removing, and viewing parties in FIFO order.
 */
public class ReservationQueue
{
    //~ Private Inner Class ..................................................

    /**
     * Represents a node in the singly linked queue.
     * Stores a party and a reference to the next node.
     */
    private static class Node
    {
        private Party data;
        private Node next;

        /**
         * Creates a new node containing the given party.
         *
         * @param data the party stored in the node
         */
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

    /**
     * Creates a new empty ReservationQueue.
     * Initializes the front and rear to null and the size to zero.
     */
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
     * @throws IllegalArgumentException if the party is null
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
     * Removes and returns the first party in the queue.
     *
     * @return the first party, or null if the queue is empty
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
     * @return the first party, or null if the queue is empty
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
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return size == 0;
    }


    /**
     * Returns the number of parties currently in the queue.
     *
     * @return the number of waiting parties
     */
    public int size()
    {
        return size;
    }


    /**
     * Checks whether a matching party exists in the queue.
     *
     * @param party the party to find
     * @return true if the party is found, false otherwise
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
     * Updates the front or rear references when necessary.
     *
     * @param party the party to remove
     * @return true if the party was removed, false otherwise
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
     * Returns all parties in the queue in FIFO order.
     *
     * @return an array containing the waiting parties
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
