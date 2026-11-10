package main;

/**
 * Controls the restaurant waitlist application.
 * Connects the model and view, handles menu selections,
 * and manages adding, seating, and canceling parties.
 *
 * @author Jasjeet Sarna
 * @version 11/09/2026
 */
public class ReservationController
{
    private final ReservationModel model;
    private final ReservationView view;
    private boolean running;

    public ReservationController(
        ReservationModel model,
        ReservationView view)
    {
        if (model == null || view == null)
        {
            throw new IllegalArgumentException(
                "Model and view are required.");
        }

        this.model = model;
        this.view = view;
    }

    public static void main(String[] args)
    {
        new ReservationController(
            new ReservationModel(),
            new ReservationView()).run();
    }

    public void run()
    {
        running = true;

        while (running)
        {
            view.showMenu();

            Integer choice = view.readMenuChoice();

            if (choice == null)
            {
                break;
            }

            switch (choice)
            {
                case 1:
                    addParty();
                    break;

                case 2:
                    viewNextParty();
                    break;

                case 3:
                    seatNextParty();
                    break;

                case 4:
                    viewWaitlist();
                    break;

                case 5:
                    cancelReservation();
                    break;

                case 6:
                    viewPartyCount();
                    break;

                case 0:
                    running = false;
                    view.showMessage("Goodbye!");
                    break;

                default:
                    view.showError(
                        "Choose an option from 0 to 6.");
            }
        }
    }

    public void addParty()
    {
        String name = view.readPartyName();

        if (name == null)
        {
            running = false;
            return;
        }

        Integer size = view.readPartySize();

        if (size == null)
        {
            running = false;
            return;
        }

        try
        {
            model.addParty(name, size);

            view.showMessage(
                "Added " + name +
                " (party of " + size + ").");
        }
        catch (IllegalArgumentException e)
        {
            view.showError(e.getMessage());
        }
    }

    public void viewNextParty()
    {
        Party party = model.getNextParty();

        if (party == null)
        {
            view.showMessage("No parties are waiting.");
        }
        else
        {
            view.showParty("Next party: ", party);
        }
    }

    public void seatNextParty()
    {
        Party party = model.seatNextParty();

        if (party == null)
        {
            view.showMessage("No parties are waiting.");
        }
        else
        {
            view.showParty("Seated: ", party);
        }
    }

    public void viewWaitlist()
    {
        Party[] parties = model.getWaitlist();

        if (parties.length == 0)
        {
            view.showMessage("No parties are waiting.");
        }
        else
        {
            view.showWaitlist(parties);
        }
    }

    public void cancelReservation()
    {
        String name = view.readPartyName();

        if (name == null)
        {
            running = false;
            return;
        }

        Integer size = view.readPartySize();

        if (size == null)
        {
            running = false;
            return;
        }

        try
        {
            if (model.cancelReservation(name, size))
            {
                view.showMessage(
                    "Reservation for " + name +
                    " (" + size + ") cancelled.");
            }
            else
            {
                view.showMessage("Reservation not found.");
            }
        }
        catch (IllegalArgumentException e)
        {
            view.showError(e.getMessage());
        }
    }

    public void viewPartyCount()
    {
        view.showPartyCount(model.getPartyCount());
    }
}

