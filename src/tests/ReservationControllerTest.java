package tests;

import java.util.Scanner;
import main.Party;
import main.ReservationController;
import main.ReservationModel;
import main.ReservationView;
import student.TestCase;

/**
 * Tests the restaurant waitlist controller.
 * Verifies adding, seating, and canceling parties,
 * handling invalid inputs, and running without crashing.
 *
 * @author Jasjeet Sarna
 * @version 11/09/2026
 */
public class ReservationControllerTest extends TestCase
{
    public void testAddSeatCancelFlow()
    {
        ReservationModel model = new ReservationModel();

        ReservationView view = new ReservationView(
            new Scanner(
                "1\nSmith\n4\n" +
                "1\nJones\n2\n" +
                "5\nJones\n2\n" +
                "3\n0\n"));

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
                "zero\n0\n2\n0\n"));

        new ReservationController(model, view).run();

        assertEquals(1, model.getPartyCount());

        assertEquals(
            new Party("Mary Jane", 2),
            model.getNextParty());
    }

    public void testEOFWithoutCrashing()
    {
        ReservationModel model = new ReservationModel();

        ReservationView view = new ReservationView(
            new Scanner("1\nSmith\n"));

        new ReservationController(model, view).run();

        assertEquals(0, model.getPartyCount());
    }
}
