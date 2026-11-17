package tests;

import main.Party;
import main.ReservationModel;
import main.Section;
import student.TestCase;

/**
 * Tests the restaurant waitlist model.
 * Verifies adding, seating, and canceling parties,
 * retrieving waitlist information, and handling invalid inputs.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/17/2026
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
        assertNull(model.getNextParty(Section.INDOOR));
        assertNull(model.seatNextParty(Section.INDOOR));
        assertEquals(0, model.getWaitlist(Section.INDOOR).length);
    }


    /**
     * Tests adding a single party to the waitlist.
     * Verifies that the party count increases and
     * the added party becomes the next party.
     */
    public void testAddParty()
    {
        model.addParty("Smith", 4, Section.INDOOR);

        assertEquals(1, model.getPartyCount());
        assertFalse(model.isWaitlistEmpty());
        assertEquals(
            new Party("Smith", 4),
            model.getNextParty(Section.INDOOR));
    }


    /**
     * Tests adding multiple parties to the waitlist.
     * Verifies the party count and that the first
     * added party remains at the front of the queue.
     */
    public void testAddMultipleParties()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Johnson", 2, Section.INDOOR);
        model.addParty("Patel", 3, Section.INDOOR);

        assertEquals(3, model.getPartyCount());
        assertEquals(
            new Party("Smith", 4),
            model.getNextParty(Section.INDOOR));
    }


    /**
     * Tests that a duplicate name and size cannot be added,
     * regardless of capitalization, without changing the waitlist.
     */
    public void testRejectDuplicateParty()
    {
        model.addParty("Smith", 4, Section.INDOOR);

        try
        {
            model.addParty("smith", 4, Section.INDOOR);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(1, model.getPartyCount());
        assertEquals(
            new Party("Smith", 4),
            model.getNextParty(Section.INDOOR));

        model.addParty("SMITH", 2, Section.INDOOR);
        assertEquals(2, model.getPartyCount());
    }


    /**
     * Tests that an existing party can be cancelled
     * using a different capitalization.
     */
    public void testCaseInsensitiveCancellation()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Jones", 2, Section.INDOOR);

        assertTrue(model.cancelReservation("sMiTh", 4));
        assertEquals(1, model.getPartyCount());
        assertEquals(
            new Party("Jones", 2),
            model.getNextParty(Section.INDOOR));
    }


    /**
     * Tests seating the next party in FIFO order.
     * Verifies that the first party is removed,
     * the count decreases, and the next party moves forward.
     */
    public void testSeatNextParty()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Johnson", 2, Section.INDOOR);

        assertEquals(
            new Party("Smith", 4),
            model.seatNextParty(Section.INDOOR));

        assertEquals(1, model.getPartyCount());
        assertEquals(
            new Party("Johnson", 2),
            model.getNextParty(Section.INDOOR));
    }


    /**
     * Tests seating a party when the waitlist is empty.
     * Verifies that null is returned and the waitlist
     * remains empty.
     */
    public void testSeatFromEmptyWaitlist()
    {
        assertNull(model.seatNextParty(Section.INDOOR));
        assertTrue(model.isWaitlistEmpty());
    }


    /**
     * Tests canceling an existing reservation.
     * Verifies that the party is removed, the count
     * decreases, and a repeated cancellation returns false.
     */
    public void testCancelReservation()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Johnson", 2, Section.INDOOR);

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
        model.addParty("Smith", 4, Section.INDOOR);

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
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Johnson", 2, Section.INDOOR);

        Party[] parties = model.getWaitlist(Section.INDOOR);

        assertEquals(2, parties.length);
        assertEquals(new Party("Smith", 4), parties[0]);
        assertEquals(new Party("Johnson", 2), parties[1]);

        parties[0] = new Party("Patel", 3);

        assertEquals(
            new Party("Smith", 4),
            model.getNextParty(Section.INDOOR));
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
            model.addParty("", 2, Section.INDOOR);
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
            model.addParty("Smith", 0, Section.INDOOR);
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

    /**
     * Tests that indoor and outdoor parties join their own lines.
     */
    public void testAddToEachSection()
    {
        assertEquals(Section.INDOOR,
            model.addParty("Smith", 4, Section.INDOOR));
        assertEquals(Section.OUTDOOR,
            model.addParty("Lee", 2, Section.OUTDOOR));

        assertEquals(1, model.getPartyCount(Section.INDOOR));
        assertEquals(1, model.getPartyCount(Section.OUTDOOR));
        assertEquals(2, model.getPartyCount());
        assertEquals(new Party("Smith", 4),
            model.getNextParty(Section.INDOOR));
        assertEquals(new Party("Lee", 2),
            model.getNextParty(Section.OUTDOOR));
    }


    /**
     * Tests that an EITHER party joins the shorter line, and that a
     * tie goes to indoor.
     */
    public void testEitherRouting()
    {
        assertEquals(Section.INDOOR,
            model.addParty("Smith", 4, Section.EITHER));
        assertEquals(Section.OUTDOOR,
            model.addParty("Lee", 2, Section.EITHER));
        assertEquals(Section.INDOOR,
            model.addParty("Kim", 3, Section.EITHER));

        model.addParty("Patel", 2, Section.INDOOR);

        assertEquals(Section.OUTDOOR,
            model.addParty("Ruiz", 5, Section.EITHER));
        assertEquals(3, model.getPartyCount(Section.INDOOR));
        assertEquals(2, model.getPartyCount(Section.OUTDOOR));
    }


    /**
     * Tests that seating from an empty section returns null even
     * when the other section has parties waiting.
     */
    public void testSeatFromEmptySection()
    {
        model.addParty("Smith", 4, Section.INDOOR);

        assertNull(model.seatNextParty(Section.OUTDOOR));
        assertNull(model.getNextParty(Section.OUTDOOR));
        assertEquals(1, model.getPartyCount());
        assertFalse(model.isWaitlistEmpty());
    }


    /**
     * Tests that each section is served in its own FIFO order.
     */
    public void testSectionsAreIndependent()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Lee", 2, Section.OUTDOOR);
        model.addParty("Kim", 3, Section.INDOOR);

        assertEquals(new Party("Lee", 2),
            model.seatNextParty(Section.OUTDOOR));
        assertEquals(new Party("Smith", 4),
            model.seatNextParty(Section.INDOOR));
        assertEquals(new Party("Kim", 3),
            model.seatNextParty(Section.INDOOR));
        assertTrue(model.isWaitlistEmpty());
    }


    /**
     * Tests that cancel finds a party in either section.
     */
    public void testCancelAcrossSections()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Lee", 2, Section.OUTDOOR);

        assertTrue(model.cancelReservation("lee", 2));
        assertEquals(0, model.getPartyCount(Section.OUTDOOR));
        assertTrue(model.cancelReservation("Smith", 4));
        assertTrue(model.isWaitlistEmpty());
    }


    /**
     * Tests that a party already waiting in one section cannot be
     * added to the other.
     */
    public void testRejectDuplicateAcrossSections()
    {
        model.addParty("Smith", 4, Section.INDOOR);

        try
        {
            model.addParty("SMITH", 4, Section.OUTDOOR);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(0, model.getPartyCount(Section.OUTDOOR));
    }


    /**
     * Tests that each section's waitlist is returned separately.
     */
    public void testGetWaitlistBySection()
    {
        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Lee", 2, Section.OUTDOOR);
        model.addParty("Ruiz", 5, Section.OUTDOOR);

        Party[] outside = model.getWaitlist(Section.OUTDOOR);

        assertEquals(2, outside.length);
        assertEquals(new Party("Lee", 2), outside[0]);
        assertEquals(new Party("Ruiz", 5), outside[1]);
        assertEquals(1, model.getWaitlist(Section.INDOOR).length);
    }


    /**
     * Tests that a null preference is rejected when adding, and that
     * a null or EITHER section is rejected when choosing a line.
     */
    public void testInvalidSection()
    {
        try
        {
            model.addParty("Smith", 4, null);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        assertEquals(0, model.getPartyCount());

        Section[] invalid = {null, Section.EITHER};

        for (Section section : invalid)
        {
            try
            {
                model.seatNextParty(section);
                fail("Expected an IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                // Expected
            }

            try
            {
                model.getWaitlist(section);
                fail("Expected an IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                // Expected
            }
        }
    }
}