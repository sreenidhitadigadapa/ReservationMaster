package main;

/**
 * The seating sections a party can wait for. INDOOR and OUTDOOR are
 * the two waitlists. EITHER is a seating preference only: a party
 * that chooses it is routed to whichever waitlist is shorter.
 *
 * @author Tyler Santiago
 * @version 11/17/2026
 */
public enum Section
{
    /** The indoor waitlist. */
    INDOOR,

    /** The outdoor waitlist. */
    OUTDOOR,

    /** No preference; join the shorter waitlist. */
    EITHER
}