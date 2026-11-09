
package main;

/**
 * Tests the ReservationModel independently of the view and controller.
 * Verifies that parties can be added, viewed, seated, and canceled
 * while maintaining the correct FIFO order.
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

        model.addParty("Smith", 4);
        model.addParty("Johnson", 2);
        model.addParty("Patel", 3);

        check(model.getPartyCount() == 3,
            "Adding parties");

        check(model.getNextParty().equals(new Party("Smith", 4)),
            "Viewing next party");

        check(model.getPartyCount() == 3,
            "Peek does not remove");

        check(model.seatNextParty().equals(new Party("Smith", 4)),
            "Seating next party");

        check(model.cancelReservation("Johnson", 2),
            "Canceling reservation");

        check(model.getNextParty().equals(new Party("Patel", 3)),
            "FIFO order after cancellation");

        check(model.seatNextParty().equals(new Party("Patel", 3)),
            "Seating final party");

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
