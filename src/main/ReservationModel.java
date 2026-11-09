
package main;

/**
 * Manages the restaurant waitlist using a ReservationQueue.
 * Handles adding parties, seating parties, canceling reservations,
 * and retrieving waitlist information.
 */
public class ReservationModel
{
    //~ Fields ................................................................

    private final ReservationQueue waitlist;


    //~ Constructors ..........................................................

    /**
     * Creates a new ReservationModel with an empty waitlist.
     */
    public ReservationModel()
    {
        waitlist = new ReservationQueue();
    }


    //~ Public Methods ........................................................

    /**
     * Adds a new party to the end of the waitlist.
     *
     * @param name the name of the party
     * @param partySize the number of people in the party
     * @throws IllegalArgumentException if the name or party size is invalid
     */
    public void addParty(String name, int partySize)
    {
        Party party = new Party(name, partySize);
        waitlist.enqueue(party);
    }


    /**
     * Returns the next party in the waitlist without removing it.
     *
     * @return the next party, or null if the waitlist is empty
     */
    public Party getNextParty()
    {
        return waitlist.peek();
    }


    /**
     * Removes and returns the next party in the waitlist.
     *
     * @return the seated party, or null if the waitlist is empty
     */
    public Party seatNextParty()
    {
        return waitlist.dequeue();
    }


    /**
     * Cancels a reservation by removing the matching party
     * from the waitlist.
     *
     * @param name the name of the party to remove
     * @param partySize the size of the party to remove
     * @return true if the party was removed, false if not found
     * @throws IllegalArgumentException if the name or party size is invalid
     */
    public boolean cancelReservation(String name, int partySize)
    {
        Party party = new Party(name, partySize);
        return waitlist.remove(party);
    }


    /**
     * Returns the number of parties currently in the waitlist.
     *
     * @return the number of waiting parties
     */
    public int getPartyCount()
    {
        return waitlist.size();
    }


    /**
     * Returns all parties currently in the waitlist in FIFO order.
     *
     * @return an array containing the waiting parties
     */
    public Party[] getWaitlist()
    {
        return waitlist.toArray();
    }


    /**
     * Checks whether the waitlist contains any parties.
     *
     * @return true if the waitlist is empty, false otherwise
     */
    public boolean isWaitlistEmpty()
    {
        return waitlist.isEmpty();
    }
}
