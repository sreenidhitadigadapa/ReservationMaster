package main;

/**
 * Controls the restaurant waitlist application.
 * Connects the model and view, handles menu selections,
 * and manages adding, seating, and canceling parties.
 *
 * @author Jasjeet Sarna
 * @version 11/17/2026
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

        Section preference = view.readSection(true);

        if (preference == null)
        {
            running = false;
            return;
        }

        try
        {
            Section joined = model.addParty(name, size, preference);

            view.showMessage(
                "Added " + name +
                " (party of " + size + ") to the " +
                view.sectionName(joined).toLowerCase() + " waitlist.");
        }
        catch (IllegalArgumentException e)
        {
            view.showError(e.getMessage());
        }
    }

    public void viewNextParty()
    {
        Section section = view.readSection(false);

        if (section == null)
        {
            running = false;
            return;
        }

        Party party = model.getNextParty(section);

        if (party == null)
        {
            view.showMessage("No parties are waiting in the "
                + view.sectionName(section).toLowerCase() + " section.");
        }
        else
        {
            view.showParty("Next party: ", party);
        }
    }

    public void seatNextParty()
    {
        Section section = view.readSection(false);

        if (section == null)
        {
            running = false;
            return;
        }

        Party party = model.seatNextParty(section);

        if (party == null)
        {
            view.showMessage("No parties are waiting in the "
                + view.sectionName(section).toLowerCase() + " section.");
        }
        else
        {
            view.showParty("Seated: ", party);
        }
    }

    public void viewWaitlist()
    {
        if (model.isWaitlistEmpty())
        {
            view.showMessage("No parties are waiting.");
        }
        else
        {
            view.showWaitlist(
                Section.INDOOR, model.getWaitlist(Section.INDOOR));
            view.showWaitlist(
                Section.OUTDOOR, model.getWaitlist(Section.OUTDOOR));
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
        view.showPartyCount(
            model.getPartyCount(Section.INDOOR),
            model.getPartyCount(Section.OUTDOOR));
    }
}