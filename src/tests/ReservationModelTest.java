
package tests;

import main.Party;
import main.ReservationModel;
import student.TestCase;

/**
 * Tests the restaurant waitlist model.
 */

public class ReservationModelTest extends TestCase
{
    private ReservationModel model;


    public void setUp()
    {
        model = new ReservationModel();
    }


    public void testEmptyModel()
    {
        assertTrue(model.isWaitlistEmpty());
        assertEquals(0, model.getPartyCount());
        assertNull(model.getNextParty());
        assertNull(model.seatNextParty());
        assertEquals(0, model.getWaitlist().length);
    }


    public void testAddParty()
    {
        model.addParty("Smith", 4);

        assertEquals(1, model.getPartyCount());
        assertFalse(model.isWaitlistEmpty());

        assertEquals(
            new Party("Smith", 4),
            model.getNextParty());
    }


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


    public void testSeatFromEmptyWaitlist()
    {
        assertNull(model.seatNextParty());
        assertTrue(model.isWaitlistEmpty());
    }


    public void testCancelReservation()
    {
        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);

        assertTrue(model.cancelReservation("Johnson", 2));
        assertEquals(1, model.getPartyCount());

        assertFalse(model.cancelReservation("Johnson", 2));
    }


    public void testCancelNotFound()
    {
        model.addParty("Smith", 4);

        assertFalse(model.cancelReservation("Jones", 3));
        assertEquals(1, model.getPartyCount());
    }


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
