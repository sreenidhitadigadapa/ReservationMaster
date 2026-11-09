
package main;

import java.util.Objects;

/**
 * Represents a party waiting for a table at a restaurant.
 * Stores the party's name and the number of people in the party.
 *
 * @author Sreenidhi Tadigadapa
 * @version 11/09/2026
 */

public class Party
{
    //~ Fields ................................................................

    private final String name;
    private final int partySize;


    //~ Constructors ..........................................................

    /**
     * Creates a new Party with a name and party size.
     *
     * @param name the name of the party
     * @param partySize the number of people in the party
     */
    public Party(String name, int partySize)
    {
        if (name == null || !name.trim().matches("[\\p{L} ]+"))
        {
            throw new IllegalArgumentException(
                "Party name must contain letters and spaces.");
        }

        if (partySize < 1)
        {
            throw new IllegalArgumentException(
                "Party size must be a positive integer.");
        }

        this.name = name.trim();
        this.partySize = partySize;
    }


    //~ Public Methods ........................................................

    /**
     * Returns the name of the party.
     *
     * @return the party name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the number of people in the party.
     *
     * @return the party size
     */
    public int getPartySize()
    {
        return partySize;
    }


    /**
     * Checks whether two parties have the same name and size.
     *
     * @param other the object to compare
     * @return true if both parties have the same name and size
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof Party))
        {
            return false;
        }

        Party party = (Party)other;

        return name.equals(party.name)
            && partySize == party.partySize;
    }


    /**
     * Returns a hash code consistent with equals.
     *
     * @return the party's hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name, partySize);
    }


    /**
     * Returns a readable description of the party.
     *
     * @return the party information
     */
    @Override
    public String toString()
    {
        return name + " (party of " + partySize + ")";
    }
}
