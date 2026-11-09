
package tests;

import main.Party;
import student.TestCase;

/**
 * Tests the Party class, including its constructor, getters,
 * equality comparison, string representation, and validation.
 */
public class PartyTest extends TestCase
{
    private Party party;


    /**
     * Sets up a party before each test.
     */
    @Override
    public void setUp()
    {
        party = new Party("Smith", 4);
    }


    /**
     * Tests that the constructor correctly initializes the
     * party name and size and that the getters return these values.
     */
    public void testConstructorAndGetters()
    {
        assertEquals("Smith", party.getName());
        assertEquals(4, party.getPartySize());
    }


    /**
     * Tests that party names containing spaces are accepted
     * and stored correctly.
     */
    public void testNameWithSpaces()
    {
        Party another = new Party("Mary Jane", 2);

        assertEquals("Mary Jane", another.getName());
        assertEquals(2, another.getPartySize());
    }


    /**
     * Tests that a party size of one is accepted as the
     * minimum valid party size.
     */
    public void testMinimumSize()
    {
        Party minimum = new Party("Jones", 1);

        assertEquals(1, minimum.getPartySize());
    }


    /**
     * Tests equality between parties with matching names and sizes.
     * Also verifies inequality for different parties, null, and
     * unrelated objects, as well as hash code consistency.
     */
    public void testEquals()
    {
        Party same = new Party("Smith", 4);
        Party differentSize = new Party("Smith", 2);
        Party differentName = new Party("Jones", 4);

        assertTrue(party.equals(same));
        assertTrue(party.equals(party));
        assertFalse(party.equals(differentSize));
        assertFalse(party.equals(differentName));
        assertFalse(party.equals(null));
        assertFalse(party.equals("Smith"));
        assertEquals(party.hashCode(), same.hashCode());
    }


    /**
     * Tests that the toString method returns the party name
     * and size in the expected format.
     */
    public void testToString()
    {
        assertEquals("Smith (party of 4)", party.toString());
    }


    /**
     * Tests that the constructor rejects invalid party names,
     * including empty, null, numeric, and whitespace-only names.
     */
    public void testInvalidName()
    {
        try
        {
            new Party("", 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            new Party(null, 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            new Party("Smith123", 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            new Party("   ", 2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }


    /**
     * Tests that the constructor rejects invalid party sizes,
     * including zero and negative values.
     */
    public void testInvalidSize()
    {
        try
        {
            new Party("Smith", 0);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            new Party("Smith", -2);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
}
