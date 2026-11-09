
package tests;

import main.Party;
import main.ReservationModel;
import student.TestCase;

/**
 * Tests the restaurant waitlist model.
 * Verifies adding, seating, and canceling parties,
 * retrieving waitlist information, and handling invalid inputs.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/09/2026
 */
public class ReservationModelTest extends TestCase
{
    private ReservationModel model;


    /**
     * Creates a new empty ReservationModel before each test.
     */
    @Override
    public void setUp()
    {
        model = new ReservationModel();
    }


    /**
     * Tests that a new model starts with an empty waitlist.
     * Verifies the party count, next party, seating behavior,
     * and returned waitlist array.
     */
    public void testEmptyModel()
    {
        assertTrue(model.isWaitlistEmpty());
        assertEquals(0, model.getPartyCount());
        assertNull(model.getNextParty());
        assertNull(model.seatNextParty());
        assertEquals(0, model.getWaitlist().length);
    }


    /**
     * Tests adding a single party to the waitlist.
     * Verifies that the party count increases and
     * the added party becomes the next party.
     */
    public void testAddParty()
    {
        model.addParty("Smith", 4);

        assertEquals(1, model.getPartyCount());
        assertFalse(model.isWaitlistEmpty());
        assertEquals(
            new Party("Smith", 4),
            model.getNextParty());
    }


    /**
     * Tests adding multiple parties to the waitlist.
     * Verifies the party count and that the first
     * added party remains at the front of the queue.
     */
    public void testAddMultipleParties()
    {
        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);
        model.addParty("Patel", 3);

        assertEquals(3, model.getPartyCount());
        assertEquals(
            new Party("Smith", 4),
            model.getNextParty());
    }


    /**
     * Tests seating the next party in FIFO order.
     * Verifies that the first party is removed,
     * the count decreases, and the next party moves forward.
     */
    public void testSeatNextParty()
    {
        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);

        assertEquals(
            new Party("Smith", 4),
            model.seatNextParty());

        assertEquals(1, model.getPartyCount());
        assertEquals(
            new Party("Johnson", 2),
            model.getNextParty());
    }


    /**
     * Tests seating a party when the waitlist is empty.
     * Verifies that null is returned and the waitlist
     * remains empty.
     */
    public void testSeatFromEmptyWaitlist()
    {
        assertNull(model.seatNextParty());
        assertTrue(model.isWaitlistEmpty());
    }


    /**
     * Tests canceling an existing reservation.
     * Verifies that the party is removed, the count
     * decreases, and a repeated cancellation returns false.
     */
    public void testCancelReservation()
    {
        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);

        assertTrue(model.cancelReservation("Johnson", 2));
        assertEquals(1, model.getPartyCount());
        assertFalse(model.cancelReservation("Johnson", 2));
    }


    /**
     * Tests canceling a reservation that does not exist.
     * Verifies that false is returned and the waitlist
     * remains unchanged.
     */
    public void testCancelNotFound()
    {
        model.addParty("Smith", 4);

        assertFalse(model.cancelReservation("Jones", 3));
        assertEquals(1, model.getPartyCount());
    }


    /**
     * Tests retrieving all parties from the waitlist.
     * Verifies that parties are returned in FIFO order
     * and modifying the returned array does not
     * change the original waitlist.
     */
    public void testGetWaitlist()
    {
        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);

        Party[] parties = model.getWaitlist();

        assertEquals(2, parties.length);
        assertEquals(new Party("Smith", 4), parties[0]);
        assertEquals(new Party("Johnson", 2), parties[1]);

        parties[0] = new Party("Patel", 3);

        assertEquals(
            new Party("Smith", 4),
            model.getNextParty());
    }


    /**
     * Tests that adding a party with an invalid name
     * throws an IllegalArgumentException.
     * Verifies that the waitlist remains empty.
     */
    public void testInvalidAddName()
    {
        try
        {
            model.addParty("", 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(0, model.getPartyCount());
    }


    /**
     * Tests that adding a party with an invalid size
     * throws an IllegalArgumentException.
     * Verifies that the waitlist remains empty.
     */
    public void testInvalidAddSize()
    {
        try
        {
            model.addParty("Smith", 0);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(0, model.getPartyCount());
    }


    /**
     * Tests that canceling a reservation with invalid
     * party information throws an IllegalArgumentException.
     * Checks both a null name and a negative party size.
     */
    public void testInvalidCancellation()
    {
        try
        {
            model.cancelReservation(null, 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            model.cancelReservation("Smith", -1);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
}
