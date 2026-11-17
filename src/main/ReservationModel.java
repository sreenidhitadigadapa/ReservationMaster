package main;

/**
 * Manages the restaurant's indoor and outdoor waitlists, each backed
 * by its own ReservationQueue. Handles adding parties (routing parties
 * with no preference to the shorter line), seating parties by section,
 * cancelling reservations from either line, and reporting counts.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/17/2026
 */
public class ReservationModel
{
    //~ Fields ................................................................

    private final ReservationQueue indoor;
    private final ReservationQueue outdoor;


    //~ Constructors ..........................................................

    /**
     * Creates a new ReservationModel with empty indoor and outdoor
     * waitlists.
     */
    public ReservationModel()
    {
        indoor = new ReservationQueue();
        outdoor = new ReservationQueue();
    }


    //~ Public Methods ........................................................

    /**
     * Adds a new party to the back of a waitlist. INDOOR and OUTDOOR
     * parties join that line. EITHER parties join the shorter line,
     * with ties going to indoor. A party with the same name (ignoring
     * case) and size already waiting in either line is rejected.
     *
     * @param name the name of the party
     * @param partySize the number of people in the party
     * @param preference the section the party wants
     * @return the section the party actually joined (INDOOR or OUTDOOR)
     * @throws IllegalArgumentException if the name or size is invalid,
     *         the preference is null, or the party is already waiting
     */
    public Section addParty(String name, int partySize, Section preference)
    {
        if (preference == null)
        {
            throw new IllegalArgumentException(
                "A seating preference is required.");
        }

        Party party = new Party(name, partySize);

        if (indoor.contains(party) || outdoor.contains(party))
        {
            throw new IllegalArgumentException(
                "This party is already on the waitlist.");
        }

        Section joined = preference;

        if (preference == Section.EITHER)
        {
            joined = outdoor.size() < indoor.size()
                ? Section.OUTDOOR
                : Section.INDOOR;
        }

        queueFor(joined).enqueue(party);
        return joined;
    }


    /**
     * Returns the next party in a section's waitlist without removing it.
     *
     * @param section INDOOR or OUTDOOR
     * @return the next party, or null if that waitlist is empty
     * @throws IllegalArgumentException if section is null or EITHER
     */
    public Party getNextParty(Section section)
    {
        return queueFor(section).peek();
    }


    /**
     * Removes and returns the next party in a section's waitlist.
     *
     * @param section INDOOR or OUTDOOR
     * @return the seated party, or null if that waitlist is empty
     * @throws IllegalArgumentException if section is null or EITHER
     */
    public Party seatNextParty(Section section)
    {
        return queueFor(section).dequeue();
    }


    /**
     * Cancels a reservation by removing the matching party from
     * whichever waitlist it is in. The indoor line is searched first.
     *
     * @param name the name of the party to remove
     * @param partySize the size of the party to remove
     * @return true if the party was removed, false if not found
     * @throws IllegalArgumentException if the name or party size is invalid
     */
    public boolean cancelReservation(String name, int partySize)
    {
        Party party = new Party(name, partySize);
        return indoor.remove(party) || outdoor.remove(party);
    }


    /**
     * Returns the number of parties waiting in one section.
     *
     * @param section INDOOR or OUTDOOR
     * @return the number of waiting parties in that section
     * @throws IllegalArgumentException if section is null or EITHER
     */
    public int getPartyCount(Section section)
    {
        return queueFor(section).size();
    }


    /**
     * Returns the number of parties waiting in both sections combined.
     *
     * @return the total number of waiting parties
     */
    public int getPartyCount()
    {
        return indoor.size() + outdoor.size();
    }


    /**
     * Returns all parties in one section's waitlist in FIFO order.
     *
     * @param section INDOOR or OUTDOOR
     * @return a new array containing that section's waiting parties
     * @throws IllegalArgumentException if section is null or EITHER
     */
    public Party[] getWaitlist(Section section)
    {
        return queueFor(section).toArray();
    }


    /**
     * Checks whether both waitlists are empty.
     *
     * @return true if no parties are waiting in either section
     */
    public boolean isWaitlistEmpty()
    {
        return indoor.isEmpty() && outdoor.isEmpty();
    }


    //~ Private Methods .......................................................

    /**
     * Returns the queue for a section.
     *
     * @param section INDOOR or OUTDOOR
     * @return that section's queue
     * @throws IllegalArgumentException if section is null or EITHER
     */
    private ReservationQueue queueFor(Section section)
    {
        if (section == Section.INDOOR)
        {
            return indoor;
        }

        if (section == Section.OUTDOOR)
        {
            return outdoor;
        }

        throw new IllegalArgumentException(
            "Section must be INDOOR or OUTDOOR.");
    }
}