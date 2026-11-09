
package main;

/**
 * Tests the model independently of the view.
 */
public class ModelDriver
{
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


    private static void check(boolean condition, String testName)
    {
        if (!condition)
        {
            throw new AssertionError(testName + " failed.");
        }

        System.out.println("PASS: " + testName);
    }
}

