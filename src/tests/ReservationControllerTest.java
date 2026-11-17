package tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import main.Party;
import main.ReservationController;
import main.ReservationModel;
import main.ReservationView;
import main.Section;
import student.TestCase;

/**
 * Tests the restaurant waitlist controller.
 * Verifies adding, seating, and canceling parties,
 * handling invalid inputs, and running without crashing.
 *
 * @author Jasjeet Sarna
 * @version 11/17/2026
 */
public class ReservationControllerTest extends TestCase
{
    public void testAddSeatCancelFlow()
    {
        ReservationModel model = new ReservationModel();

        ReservationView view = new ReservationView(
            new Scanner(
                "1\nSmith\n4\ni\n" +
                "1\nJones\n2\ni\n" +
                "5\nJones\n2\n" +
                "3\ni\n0\n"));

        new ReservationController(model, view).run();

        assertEquals(0, model.getPartyCount());
        assertTrue(model.isWaitlistEmpty());
    }

    public void testInvalidInputReprompts()
    {
        ReservationModel model = new ReservationModel();

        ReservationView view = new ReservationView(
            new Scanner(
                "abc\n99\n" +
                "1\n123\nMary Jane\n" +
                "zero\n0\n2\n" +
                "x\ne\n0\n"));

        new ReservationController(model, view).run();

        assertEquals(1, model.getPartyCount());

        assertEquals(
            new Party("Mary Jane", 2),
            model.getNextParty(Section.INDOOR));
    }

    public void testEOFWithoutCrashing()
    {
        ReservationModel model = new ReservationModel();

        ReservationView view = new ReservationView(
            new Scanner("1\nSmith\n"));

        new ReservationController(model, view).run();

        assertEquals(0, model.getPartyCount());
    }

    /**
     * Runs a session that uses both sections: routing an EITHER party,
     * seating from each line, cancelling across lines, a bad section
     * answer, and the counts and waitlist display.
     */
    public void testIndoorOutdoorSession()
    {
        ReservationModel model = new ReservationModel();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ReservationView view = new ReservationView(
            new Scanner(
                "1\nSmith\n4\ni\n" +
                "1\nLee\n2\noutdoor\n" +
                "1\nKim\n3\nback\nE\n" +
                "1\nRuiz\n5\nO\n" +
                "4\n6\n" +
                "2\ne\no\n" +
                "3\no\n" +
                "5\nruiz\n5\n" +
                "3\no\n" +
                "6\n0\n"),
            new PrintStream(output, true));

        new ReservationController(model, view).run();

        String text = output.toString();

        assertTrue(text.contains("Added Smith (party of 4) to the indoor waitlist."));
        assertTrue(text.contains("Added Lee (party of 2) to the outdoor waitlist."));
        assertTrue(text.contains("Added Kim (party of 3) to the indoor waitlist."));
        assertTrue(text.contains("Enter I, O, or E"));
        assertTrue(text.contains("Indoor waitlist (front to back):"));
        assertTrue(text.contains("Outdoor waitlist (front to back):"));
        assertTrue(text.contains("Indoor: 2, Outdoor: 2, Total: 4"));
        assertTrue(text.contains("Enter I or O"));
        assertTrue(text.contains("Next party: Lee (party of 2)"));
        assertTrue(text.contains("Seated: Lee (party of 2)"));
        assertTrue(text.contains("Reservation for ruiz (5) cancelled."));
        assertTrue(text.contains("No parties are waiting in the outdoor section."));
        assertTrue(text.contains("Indoor: 2, Outdoor: 0, Total: 2"));

        assertEquals(2, model.getPartyCount(Section.INDOOR));
        assertEquals(0, model.getPartyCount(Section.OUTDOOR));
    }
}