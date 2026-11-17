package main;

/**
 * Tests the ReservationModel independently of the view and controller.
 * Verifies that parties can be added, viewed, seated, and canceled
 * while maintaining the correct FIFO order.
 * 
 * @author Sreenidhi Tadigadapa
 * @version 11/17/2026
 */
public class ModelDriver
{
    /**
     * Runs a series of checks to verify that the ReservationModel
     * works correctly without using the view or controller.
     * Tests adding parties, viewing the next party, seating parties,
     * canceling reservations, and checking if the waitlist is empty.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        ReservationModel model = new ReservationModel();

        model.addParty("Smith", 4, Section.INDOOR);
        model.addParty("Johnson", 2, Section.INDOOR);
        model.addParty("Patel", 3, Section.INDOOR);

        check(model.getPartyCount() == 3,
            "Adding parties");

        check(model.getNextParty(Section.INDOOR)
            .equals(new Party("Smith", 4)),
            "Viewing next party");

        check(model.getPartyCount() == 3,
            "Peek does not remove");

        check(model.seatNextParty(Section.INDOOR)
            .equals(new Party("Smith", 4)),
            "Seating next party");

        check(model.cancelReservation("Johnson", 2),
            "Canceling reservation");

        check(model.getNextParty(Section.INDOOR)
            .equals(new Party("Patel", 3)),
            "FIFO order after cancellation");

        check(model.addParty("Lee", 2, Section.OUTDOOR) == Section.OUTDOOR,
            "Adding to the outdoor waitlist");

        check(model.addParty("Kim", 5, Section.EITHER) == Section.INDOOR,
            "Either party joins the shorter line (tie goes indoor)");

        check(model.addParty("Ruiz", 2, Section.EITHER) == Section.OUTDOOR,
            "Either party joins the shorter line");

        check(model.cancelReservation("Lee", 2),
            "Canceling from the outdoor waitlist");

        check(model.seatNextParty(Section.INDOOR)
            .equals(new Party("Patel", 3)),
            "Seating indoor party");

        check(model.seatNextParty(Section.INDOOR)
            .equals(new Party("Kim", 5)),
            "Seating second indoor party");

        check(model.seatNextParty(Section.OUTDOOR)
            .equals(new Party("Ruiz", 2)),
            "Seating outdoor party");

        check(model.isWaitlistEmpty(),
            "Empty waitlist");

        System.out.println("All model checks passed!");
    }


    /**
     * Checks whether a test condition is true.
     * Prints a PASS message if the condition is true.
     * Throws an AssertionError if the condition is false.
     *
     * @param condition the condition being tested
     * @param testName the name of the test being performed
     * @throws AssertionError if the condition is false
     */
    private static void check(boolean condition, String testName)
    {
        if (!condition)
        {
            throw new AssertionError(testName + " failed.");
        }

        System.out.println("PASS: " + testName);
    }
}