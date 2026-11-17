package main;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all console input and output for Reservation Master.
 * This is the only class that reads from a Scanner or prints.
 * It turns raw text into typed values (a menu choice, a party name,
 * and a party size) and re-prompts until the text is well formed.
 * It never decides what a menu choice means and never changes the
 * waitlist; the controller does both.
 *
 * Every read method returns null if input runs out, so the
 * controller can stop cleanly instead of crashing.
 *
 * @author Tyler Santiago (tylermsantiago)
 * @version 11/17/2026
 */
public class ReservationView
{
    //~ Fields ................................................................

    private final Scanner scanner;
    private final PrintStream out;


    //~ Constructors ..........................................................

    /**
     * Creates a view that reads from standard input and prints to
     * standard output.
     */
    public ReservationView()
    {
        this(new Scanner(System.in), System.out);
    }


    /**
     * Creates a view that reads from the given Scanner and prints to
     * standard output. Used by tests to script user input.
     *
     * @param scanner the source of user input
     * @throws IllegalArgumentException if scanner is null
     */
    public ReservationView(Scanner scanner)
    {
        this(scanner, System.out);
    }


    /**
     * Creates a view that reads from the given Scanner and prints to
     * the given stream. Used by tests to capture output.
     *
     * @param scanner the source of user input
     * @param out where output is printed
     * @throws IllegalArgumentException if scanner or out is null
     */
    public ReservationView(Scanner scanner, PrintStream out)
    {
        if (scanner == null || out == null)
        {
            throw new IllegalArgumentException(
                "Scanner and output stream are required.");
        }

        this.scanner = scanner;
        this.out = out;
    }


    //~ Output Methods ........................................................

    /**
     * Prints the main menu.
     */
    public void showMenu()
    {
        out.println();
        out.println("=== Reservation Master ===");
        out.println("1. Add party");
        out.println("2. View next party");
        out.println("3. Seat next party");
        out.println("4. View waitlist");
        out.println("5. Cancel reservation");
        out.println("6. View party count");
        out.println("0. Exit");
    }


    /**
     * Prints a plain message.
     *
     * @param message the message to show
     */
    public void showMessage(String message)
    {
        out.println(message);
    }


    /**
     * Prints an error message.
     *
     * @param message the error to show
     */
    public void showError(String message)
    {
        out.println("Error: " + message);
    }


    /**
     * Prints a single party with a label in front of it.
     *
     * @param label text to show before the party, such as "Seated: "
     * @param party the party to show
     */
    public void showParty(String label, Party party)
    {
        out.println(label + party);
    }


    /**
     * Prints one section's waitlist in order, numbered from the front.
     *
     * @param section the section the parties are waiting for
     * @param parties that section's waiting parties in FIFO order
     */
    public void showWaitlist(Section section, Party[] parties)
    {
        out.println(sectionName(section) + " waitlist (front to back):");

        if (parties.length == 0)
        {
            out.println("  (empty)");
        }

        for (int i = 0; i < parties.length; i++)
        {
            out.println("  " + (i + 1) + ". " + parties[i]);
        }
    }


    /**
     * Prints how many parties are waiting in each section and in total.
     *
     * @param indoorCount the number of parties waiting indoors
     * @param outdoorCount the number of parties waiting outdoors
     */
    public void showPartyCount(int indoorCount, int outdoorCount)
    {
        int total = indoorCount + outdoorCount;

        if (total == 0)
        {
            out.println("No parties are waiting.");
            return;
        }

        out.println("Indoor: " + indoorCount
            + ", Outdoor: " + outdoorCount
            + ", Total: " + total
            + (total == 1 ? " party is waiting." : " parties are waiting."));
    }


    /**
     * Returns a readable name for a section.
     *
     * @param section the section
     * @return "Indoor", "Outdoor", or "Either"
     */
    public String sectionName(Section section)
    {
        switch (section)
        {
            case INDOOR:
                return "Indoor";

            case OUTDOOR:
                return "Outdoor";

            default:
                return "Either";
        }
    }


    //~ Input Methods .........................................................

    /**
     * Reads a menu choice. Re-prompts until the user types a whole
     * number. The view does not check whether the number is a real
     * menu option; the controller does that.
     *
     * @return the number typed, or null if input ran out
     */
    public Integer readMenuChoice()
    {
        while (true)
        {
            String line = prompt("Choose an option: ");

            if (line == null)
            {
                return null;
            }

            Integer value = parseInt(line);

            if (value != null)
            {
                return value;
            }

            showError("Enter the number of a menu option, for example: 1");
        }
    }


    /**
     * Reads a party name. Re-prompts until the name is not blank and
     * contains only letters and spaces. Leading and trailing spaces
     * are removed.
     *
     * @return the trimmed name, or null if input ran out
     */
    public String readPartyName()
    {
        while (true)
        {
            String line = prompt("Party name: ");

            if (line == null)
            {
                return null;
            }

            String name = line.trim();

            if (name.isEmpty())
            {
                showError("A name is required, for example: Smith");
            }
            else if (!name.matches("[\\p{L} ]+"))
            {
                showError("A name may contain only letters and spaces, "
                    + "for example: Mary Jane");
            }
            else
            {
                return name;
            }
        }
    }



    /**
     * Reads a party size. Re-prompts until the user types a whole
     * number between 1 and 20, inclusive.
     *
     * @return the party size, or null if input ran out
     */
    public Integer readPartySize()
    {
        while (true)
        {
            String line = prompt("Party size: ");
    
            if (line == null)
            {
                return null;
            }
    
            Integer size = parseInt(line);
    
            if (size != null && size >= 1 && size <= 20)
            {
                return size;
            }
    
            showError("Party size must be a whole number between 1 and 20.");
        }
    }



    /**
     * Reads a seating section. Accepts I or indoor, O or outdoor, and,
     * when allowEither is true, E or either, in any capitalization.
     * Re-prompts on anything else.
     *
     * @param allowEither whether "either" is an acceptable answer
     *        (true when adding a party, false when choosing a line)
     * @return the section chosen, or null if input ran out
     */
    public Section readSection(boolean allowEither)
    {
        String text = allowEither
            ? "Section (I = indoor, O = outdoor, E = either): "
            : "Section (I = indoor, O = outdoor): ";

        while (true)
        {
            String line = prompt(text);

            if (line == null)
            {
                return null;
            }

            String answer = line.trim().toLowerCase();

            if (answer.equals("i") || answer.equals("indoor"))
            {
                return Section.INDOOR;
            }

            if (answer.equals("o") || answer.equals("outdoor"))
            {
                return Section.OUTDOOR;
            }

            if (allowEither
                && (answer.equals("e") || answer.equals("either")))
            {
                return Section.EITHER;
            }

            showError(allowEither
                ? "Enter I, O, or E, for example: I"
                : "Enter I or O, for example: I");
        }
    }


    //~ Private Methods .......................................................

    /**
     * Prints a prompt and reads one line of input.
     *
     * @param text the prompt to print
     * @return the line typed, or null if input ran out
     */
    private String prompt(String text)
    {
        out.print(text);

        if (!scanner.hasNextLine())
        {
            out.println();
            return null;
        }

        return scanner.nextLine();
    }


    /**
     * Converts text to an int, ignoring surrounding spaces.
     *
     * @param text the text to convert
     * @return the number, or null if the text is not a whole number
     *         that fits in an int
     */
    private Integer parseInt(String text)
    {
        try
        {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}