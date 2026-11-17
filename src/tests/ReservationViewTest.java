package tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import main.Party;
import main.ReservationView;
import main.Section;
import student.TestCase;

/**
 * Tests the ReservationView class. Each test scripts the user's
 * input with a Scanner and captures what the view prints, so no
 * real console is needed. Verifies input parsing, re-prompting on
 * bad input, clean handling of end of input, and display output.
 *
 * @author Tyler Santiago (tylermsantiago)
 * @version 11/17/2026
 */
public class ReservationViewTest extends TestCase
{
    //~ Fields ................................................................

    private ByteArrayOutputStream output;


    //~ Setup .................................................................

    /**
     * Creates a fresh output buffer before each test.
     */
    @Override
    public void setUp()
    {
        output = new ByteArrayOutputStream();
    }


    /**
     * Creates a view that reads the given text and prints into the
     * output buffer.
     *
     * @param input the scripted user input
     * @return the view
     */
    private ReservationView viewWith(String input)
    {
        return new ReservationView(
            new Scanner(input),
            new PrintStream(output, true));
    }


    /**
     * Returns everything the view has printed so far.
     *
     * @return the captured output
     */
    private String printed()
    {
        return output.toString();
    }


    //~ Public Methods ........................................................

    /**
     * Tests that the constructor rejects a null Scanner or stream.
     */
    public void testConstructorRejectsNull()
    {
        try
        {
            new ReservationView(null);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }

        try
        {
            new ReservationView(new Scanner(""), null);
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }


    /**
     * Tests reading a valid menu choice, including surrounding spaces.
     */
    public void testReadMenuChoice()
    {
        ReservationView view = viewWith("3\n  5  \n");

        assertEquals(Integer.valueOf(3), view.readMenuChoice());
        assertEquals(Integer.valueOf(5), view.readMenuChoice());
    }


    /**
     * Tests that non-numeric menu input is rejected and re-prompted.
     * Out-of-range numbers are returned, since the controller checks
     * the range.
     */
    public void testReadMenuChoiceReprompts()
    {
        ReservationView view = viewWith("abc\n\n2.5\n99\n");

        assertEquals(Integer.valueOf(99), view.readMenuChoice());
        assertTrue(printed().contains("Enter the number of a menu option"));
    }


    /**
     * Tests that reading a menu choice returns null at end of input.
     */
    public void testReadMenuChoiceEndOfInput()
    {
        assertNull(viewWith("").readMenuChoice());
        assertNull(viewWith("abc\n").readMenuChoice());
    }


    /**
     * Tests reading valid names, including spaces that get trimmed.
     */
    public void testReadPartyName()
    {
        ReservationView view = viewWith("Smith\n  Mary Jane  \n");

        assertEquals("Smith", view.readPartyName());
        assertEquals("Mary Jane", view.readPartyName());
    }


    /**
     * Tests that blank names and names with digits or symbols are
     * rejected and re-prompted.
     */
    public void testReadPartyNameReprompts()
    {
        ReservationView view = viewWith("\n   \n123\nSm1th\nO'Neil!\nPatel\n");

        assertEquals("Patel", view.readPartyName());
        assertTrue(printed().contains("A name is required"));
        assertTrue(printed().contains("only letters and spaces"));
    }


    /**
     * Tests that reading a name returns null at end of input.
     */
    public void testReadPartyNameEndOfInput()
    {
        assertNull(viewWith("").readPartyName());
    }


    /**
     * Tests reading a valid party size, including the minimum of 1
     * and the maximum of 20.
     */
    public void testReadPartySize()
    {
        ReservationView view = viewWith("4\n1\n20\n");

        assertEquals(Integer.valueOf(4), view.readPartySize());
        assertEquals(Integer.valueOf(1), view.readPartySize());
        assertEquals(Integer.valueOf(20), view.readPartySize());
    }


    /**
     * Tests that words, zero, negatives, decimals, sizes above 20,
     * and numbers too large for an int are rejected and re-prompted.
     */
    public void testReadPartySizeReprompts()
    {
        ReservationView view =
            viewWith("four\n0\n-2\n2.5\n21\n99999999999\n\n3\n");

        assertEquals(Integer.valueOf(3), view.readPartySize());
        assertTrue(printed().contains("whole number between 1 and 20"));
    }


    /**
     * Tests that reading a size returns null at end of input.
     */
    public void testReadPartySizeEndOfInput()
    {
        assertNull(viewWith("").readPartySize());
        assertNull(viewWith("zero\n").readPartySize());
    }


    /**
     * Tests that the menu lists every option.
     */
    public void testShowMenu()
    {
        viewWith("").showMenu();

        String text = printed();
        assertTrue(text.contains("1. Add party"));
        assertTrue(text.contains("5. Cancel reservation"));
        assertTrue(text.contains("6. View party count"));
        assertTrue(text.contains("0. Exit"));
    }


    /**
     * Tests showing messages, errors, and a single party.
     */
    public void testShowMessageErrorParty()
    {
        ReservationView view = viewWith("");

        view.showMessage("Hello");
        view.showError("Bad input");
        view.showParty("Seated: ", new Party("Smith", 4));

        String text = printed();
        assertTrue(text.contains("Hello"));
        assertTrue(text.contains("Error: Bad input"));
        assertTrue(text.contains("Seated: Smith (party of 4)"));
    }


    /**
     * Tests that a section's waitlist is printed with its label and
     * numbered from the front, and that an empty section says so.
     */
    public void testShowWaitlist()
    {
        ReservationView view = viewWith("");

        view.showWaitlist(Section.INDOOR, new Party[] {
            new Party("Smith", 4),
            new Party("Johnson", 2)});
        view.showWaitlist(Section.OUTDOOR, new Party[0]);

        String text = printed();
        assertTrue(text.contains("Indoor waitlist (front to back):"));
        assertTrue(text.contains("1. Smith (party of 4)"));
        assertTrue(text.contains("2. Johnson (party of 2)"));
        assertTrue(text.indexOf("Smith") < text.indexOf("Johnson"));
        assertTrue(text.contains("Outdoor waitlist (front to back):"));
        assertTrue(text.contains("(empty)"));
    }


    /**
     * Tests the party count wording for no parties, one party, and
     * several parties split across sections.
     */
    public void testShowPartyCount()
    {
        ReservationView view = viewWith("");

        view.showPartyCount(0, 0);
        view.showPartyCount(0, 1);
        view.showPartyCount(2, 1);

        String text = printed();
        assertTrue(text.contains("No parties are waiting."));
        assertTrue(text.contains("Indoor: 0, Outdoor: 1, Total: 1 party is waiting."));
        assertTrue(text.contains("Indoor: 2, Outdoor: 1, Total: 3 parties are waiting."));
    }


    /**
     * Tests reading every valid section answer, in any capitalization
     * and with surrounding spaces.
     */
    public void testReadSection()
    {
        ReservationView view =
            viewWith("i\nINDOOR\n o \nOutdoor\nE\neither\n");

        assertEquals(Section.INDOOR, view.readSection(true));
        assertEquals(Section.INDOOR, view.readSection(true));
        assertEquals(Section.OUTDOOR, view.readSection(true));
        assertEquals(Section.OUTDOOR, view.readSection(true));
        assertEquals(Section.EITHER, view.readSection(true));
        assertEquals(Section.EITHER, view.readSection(true));
    }


    /**
     * Tests that bad section answers are re-prompted, and that
     * "either" is rejected when choosing a single line.
     */
    public void testReadSectionReprompts()
    {
        ReservationView view = viewWith("\nback\n1\no\n");
        assertEquals(Section.OUTDOOR, view.readSection(true));
        assertTrue(printed().contains("Enter I, O, or E"));

        ReservationView lineOnly = viewWith("e\neither\ni\n");
        assertEquals(Section.INDOOR, lineOnly.readSection(false));
        assertTrue(printed().contains("Enter I or O"));
    }


    /**
     * Tests that reading a section returns null at end of input.
     */
    public void testReadSectionEndOfInput()
    {
        assertNull(viewWith("").readSection(true));
        assertNull(viewWith("x\n").readSection(false));
    }


    /**
     * Tests the readable section names.
     */
    public void testSectionName()
    {
        ReservationView view = viewWith("");

        assertEquals("Indoor", view.sectionName(Section.INDOOR));
        assertEquals("Outdoor", view.sectionName(Section.OUTDOOR));
        assertEquals("Either", view.sectionName(Section.EITHER));
    }
}