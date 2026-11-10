# Reservation Master

A console waitlist for a restaurant, built on a singly linked FIFO queue. Hosts can add, view, seat, and cancel parties.

## How to run

- **Model only (no view):** run `main.ModelDriver`, or run `ReservationModelTest` and `ReservationQueueTest` as JUnit tests.
- **Thin view:** run `main.ReservationController`. The console view goes through the controller to reach the model.
- **All tests:** run the `tests` package as JUnit tests. All 57 tests pass, including the JUnit tests for the core queue methods. This needs `student.jar` on the build path.

The program survives the bad input named in our scope:
- a non-number or out-of-range menu choice;
- a blank name, or a name with digits or symbols;
- a party size that is not a number, or is zero or negative.

In each case it re-prompts and shows an example of valid input.

## Reflection

### What changed from our specification?

Each change is listed with what the spec said, what we built, and what prompted it.

- **Classes: `Node`.**
  - Spec: a separate `Node<Party>` class.
  - Built: a private class nested inside `ReservationQueue`.
  - Prompted by: the spec's rule that no `Node` ever leaves the queue. Nesting it makes the compiler enforce that.
- **Signatures: `Party.hashCode()`.**
  - Spec: only listed `equals`.
  - Built: added `hashCode()` as well.
  - Prompted by: Java's rule that `equals` and `hashCode` must agree.
- **Signatures: view read methods.**
  - Spec: return `int` and `String`.
  - Built: return `Integer` and `String`, using `null` to mean input ran out.
  - Prompted by: the controller test where input ends mid-entry, which crashed with `NoSuchElementException` until we handled it.
- **Signatures: menu numbering.**
  - Spec: choices 1 to N.
  - Built: 1 to 6 for actions, 0 for Exit.
  - Prompted by: the change feature. New menu options can be added without renumbering Exit.
- **Validation: names.**
  - Scope: letters only.
  - Built: letters and spaces.
  - Prompted by: writing tests, where valid names like "Mary Jane" were rejected.
- **Tests.**
  - Spec: model tests only.
  - Built: model, view, and controller tests, using scripted `Scanner` input.
  - Prompted by: the "runs without crashing" requirement, which is easier to prove with a test than by hand.

### Did the model really stand alone?

Yes. The model classes import nothing from the view or controller, and they run on their own through `ModelDriver` and the model tests. Nothing had to cross the model/view line. Two small overlaps remain:
- The name rule is checked in both the view and `Party`.
- `Party.toString()` decides the display format.

### What happened to our unknowns?

- **Single queue or several queues:** we used a single queue for the MVP. Managing several queues at once is still open, and our change feature will answer it.
- **Removing a party from the middle of a FIFO queue:** solved with `ReservationQueue.remove(Party)`.

### What doesn't work yet?

All MVP features work and all tests pass, but:
- Cancelling requires an exact, case-sensitive name and the exact party size.
- Duplicate parties are allowed.
- There is no maximum party size.
- `PartyTest` is missing the spec's `toString()` test for a name with spaces.

## Next Steps

### Finishing the MVP (in order)

1. Make name matching case-insensitive when cancelling (`Party` constructor and `equals`).
2. Reject duplicate parties (`ReservationModel.addParty`).
3. Add a maximum party size of 20 (`Party` constructor and `ReservationView.readPartySize`).
4. Add the missing `PartyTest` case.

### Change feature: indoor and outdoor waitlists

There will be two queues. A party chooses indoor, outdoor, or either. "Either" parties join the shorter line, with ties going to indoor. The host seats parties by section, and cancel searches both lines.

**Predicted to change:**
- **`ReservationModel`:**
  - replace `waitlist` with `indoor` and `outdoor` queues;
  - `addParty(name, size, Section)` applies the routing rule;
  - `getNextParty(Section)`, `seatNextParty(Section)`, `getWaitlist(Section)`, and `getPartyCount(Section)` work on one section;
  - `cancelReservation` searches both queues.
- **New `Section` enum (model):** `INDOOR`, `OUTDOOR`, `EITHER`.
- **`ReservationView`:**
  - new `readSection()`;
  - `showMenu()` text updated;
  - `showWaitlist(Section, Party[])` labels each section.
- **`ReservationController`:**
  - `addParty()`, `viewNextParty()`, `seatNextParty()`, `viewWaitlist()`, and `viewPartyCount()` ask the view for a section and pass it to the model.

**Predicted not to change:**
- `ReservationQueue` and its `Node`. Two lines are just two instances of the queue.
- `Party`. A party's section is decided by which queue it is in.

### Stretch goals

- **Doing:** multiple queues (the change feature above).
- **If time allows:** estimated wait times, through a new `ReservationModel.getEstimatedWait(Section, int position)` method.
- **Dropping:** reservation types and table availability, because both are too large for the time left.

### New tests

- **Model:**
  - routing for indoor, outdoor, and either, including ties;
  - seating from an empty section while the other section has parties;
  - cancelling across both sections;
  - per-section counts;
  - a `null` section throws `IllegalArgumentException`.
- **View:** `readSection()` with valid input in any capitalization, bad input, and end of input.
- **Controller:** a scripted session that uses both sections, including a bad section choice.
- **MVP fixes:** case-insensitive cancel, duplicate rejection, and the size limit (20 accepted, 21 rejected).

### Who does what

| Item | Owner |
|---|---|
| MVP fixes, `Section`, routing, and model tests | Sreenidhi |
| View updates and view tests | Tyler |
| Controller updates, integration, and controller tests | Jas |

## Attribution

<!-- TEAM: replace every [bracketed] entry before submitting. -->

| File | Who | How |
|---|---|---|
| `Party.java` | Sreenidhi | reviewed / revised / tested |
| `ReservationQueue.java` | Sreenidhi | reviewed / revised / tested |
| `ReservationModel.java` | Sreenidhi | reviewed / revised / tested |
| `ModelDriver.java` | Sreenidhi | wrote / reviewed |
| `ReservationView.java` | Tyler | reviewed / revised / tested |
| `ReservationController.java` | Jas | Wrote; Javadoc added with GenAI |
| `PartyTest.java` | Sreenidhi | wrote / reviewed |
| `ReservationQueueTest.java` | Sreenidhi | wrote / reviewed / revised |
| `ReservationModelTest.java` | Sreenidhi | Wrote / reviewed |
| `ReservationViewTest.java` | Tyler | reviewed / revised |
| `ReservationControllerTest.java` | Jas | wrote / reviewed / revised |

### GenAI note

We used Claude to check over and help write javadoc comments for these files and parts:
- `ReservationQueueTest`;
- `testFullMvpPath`;
- the controller's Javadoc;
- this README.