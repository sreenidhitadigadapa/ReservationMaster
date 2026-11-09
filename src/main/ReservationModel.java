
package main;

/**
 * Manages the restaurant waitlist using a ReservationQueue.
 * Handles adding parties, seating parties, canceling reservations,
 * and retrieving waitlist information.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/09/2026
 */

public class ReservationModel
{
    //~ Fields ................................................................

    private final ReservationQueue waitlist;


    //~ Constructors ..........................................................

    public ReservationModel()
    {
        waitlist = new ReservationQueue();
    }


    //~ Public Methods ........................................................

    public void addParty(String name, int partySize)
    {
        Party party = new Party(name, partySize);
        waitlist.enqueue(party);
    }


    public Party getNextParty()
    {
        return waitlist.peek();
    }


    public Party seatNextParty()
    {
        return waitlist.dequeue();
    }


    public boolean cancelReservation(String name, int partySize)
    {
        Party party = new Party(name, partySize);
        return waitlist.remove(party);
    }


    public int getPartyCount()
    {
        return waitlist.size();
    }


    public Party[] getWaitlist()
    {
        return waitlist.toArray();
    }


    public boolean isWaitlistEmpty()
    {
        return waitlist.isEmpty();
    }
}
