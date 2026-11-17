# Reservation Master

A console waitlist for a restaurant with separate indoor and outdoor lines, each built on a singly linked FIFO queue. Hosts can add, view, seat, and cancel parties.

## How to run

- **Model only (no view):** run `main.ModelDriver`, or run `ReservationModelTest` and `ReservationQueueTest` as JUnit tests.
- **Thin view:** run `main.ReservationController`. The console view goes through the controller to reach the model.
- **All tests:** run the `tests` package as JUnit tests. All 74 tests pass, including the JUnit tests for the core queue methods. This needs `student.jar` on the build path.

The program survives the bad input named in our scope:
- a non-number or out-of-range menu choice;
- a blank name, or a name with digits or symbols;
- a party size that is not a number, or is zero, negative, or above 20;
- a section answer other than I, O, or E (or E when choosing a line to seat from).

In each case it re-prompts and shows an example of valid input.

## Reflection

In Deliverable 4 we first finished the four MVP fixes, then added our change feature: **separate indoor and outdoor waitlists**. A party chooses indoor, outdoor, or either. "Either" joins the shorter line, with ties going to indoor. The host seats and views the next party by section, sees both lines and both counts at once, and can cancel a party without knowing which line it is in.

### How did our design absorb the change?

**Classes the change feature modified compared with our D3 prediction:**

| Class | Predicted | What actually happened |
|---|---|---|
| `Section` (new, model) | New enum `INDOOR`, `OUTDOOR`, `EITHER` | Matched. |
| `ReservationModel` | Two queues; `addParty(name, size, Section)`; `getNextParty`, `seatNextParty`, `getWaitlist`, `getPartyCount` take a `Section`; cancel searches both | Matched, except `addParty` returns the `Section` the party joined instead of `void`. We also kept a no-argument `getPartyCount()` for the total, and added a private `queueFor(Section)` helper. |
| `ReservationView` | `readSection()`, `showMenu()` updated, `showWaitlist(Section, Party[])` | `readSection` became `readSection(boolean allowEither)`. `showWaitlist(Section, Party[])` matched. `showMenu()` **did not change**. `showPartyCount(int)` became `showPartyCount(int, int)`, and a new `sectionName(Section)` turns a section into display text. |
| `ReservationController` | `addParty`, `viewNextParty`, `seatNextParty`, `viewWaitlist`, `viewPartyCount` ask for a section | The first three ask. `viewWaitlist` and `viewPartyCount` show both sections without asking. `cancelReservation` didn't change. |
| `ModelDriver` | Not mentioned | Had to change, because the model's methods now take a `Section`. |
| `Party` | Should not change | **Did not change.** |
| `ReservationQueue` and `Node` | Should not change | **Did not change.** Two lines are two instances of the same queue. |

**What matched:** every class we predicted would change did, and the two we said would stay untouched stayed untouched. Because `ReservationQueue` holds no restaurant logic, the second line cost no new queue code. Cancelling across both lines was one line: `indoor.remove(party) || outdoor.remove(party)`.

**What surprised us:**
- **The controller needed to know where an "either" party went,** so it could tell the host. That is why `addParty` returns a `Section`.
- **"Either" is only valid when adding.** You can't seat "the next either party," so `readSection` needed a flag and the model rejects `EITHER` everywhere except `addParty`.
- **The menu didn't need to change,** because the section is asked after the host picks an action.
- **The model's signature changes rippled into code we hadn't listed:** `ModelDriver` and every existing call in `ReservationModelTest` and `ReservationControllerTest`.
- **The D4 duplicate rule interacted with the feature.** It had to check both lines, so a party can't wait indoors and outdoors at the same time.

**The MVP fixes** touched only the methods we predicted: the `Party` constructor and `equals`, `ReservationModel.addParty`, and `ReservationView.readPartySize`. The one exception was `Party.hashCode`, which had to change so it agrees with the now case-insensitive `equals`.

### Did the change cross the model/view line?

Yes, in one way we predicted. The view had to learn a new model type, `Section`: `readSection` returns it, `showWaitlist` takes it, and `sectionName` switches on its values. This is acceptable because `Section` is a fixed enum the view can't use to change the waitlist, and the view still has no reference to the model or the queues. The cost is that adding a third section (for example, a patio) would mean editing the view as well as the model.

Two rules are now written on both sides of the line:
- **"Either" is only allowed when adding.** The view re-prompts, and the model throws `IllegalArgumentException` if bypassed.
- **The maximum party size of 20.** It is in `Party` and in `ReservationView.readPartySize`.

The ripple into `ModelDriver` and the existing tests stayed inside the model's callers. It never reached `Party` or `ReservationQueue`, and the view never saw the queues.

### What changed from our D3 plan?

- **Signatures:**
  - `addParty` returns `Section` instead of `void`, so the host can be told which line a party joined.
  - `readSection()` became `readSection(boolean allowEither)`, because seating and viewing must not accept "either."
  - `showPartyCount(int)` became `showPartyCount(int, int)`, so it can show both counts and a total.
  - We kept `getPartyCount()` (total) and `isWaitlistEmpty()` (both lines) without a section, because the controller needs a whole-restaurant answer.
- **Features:** `viewWaitlist` and `viewPartyCount` show both sections at once instead of asking for one. A host checking the line wants to see the whole floor, and it saves a prompt.
- **Validation:** `EITHER` and `null` are rejected for seating, viewing, and counting, in the model (exception) and in the view (re-prompt). Duplicates are checked across both lines.
- **MVP fix 1:** the plan said the `Party` constructor and `equals`; we changed `equals` and `hashCode`.
- **Tests:**
  - All the planned section tests were written:
    - `ReservationModelTest`: routing (including ties), seating from an empty section, sections staying independent, cancel across sections, duplicates across sections, per-section waitlists, and invalid sections;
    - `ReservationViewTest`: `readSection` with valid, bad, and end-of-input answers;
    - `ReservationControllerTest.testIndoorOutdoorSession`: a scripted session using both sections and a bad section answer.
  - `ReservationViewTest` is back in the project with these tests added. Its party size tests now cover 20 being accepted and 21 being rejected.
  - Existing model and controller tests were rewritten to pass a section.

### What doesn't work yet?

- **"Either" routing counts parties, not people.** A line with one party of 12 counts as shorter than a line with two parties of 2.
- **A party can't switch sections without losing its place.** It has to cancel and rejoin at the back.
- **Estimated wait times are not implemented.**
- **The cancel message repeats the name as typed,** for example "Reservation for ruiz (5) cancelled."
- **Extra spaces inside a name make a different party** ("Mary  Jane" vs. "Mary Jane").
- **The section values, the "either" rule, and the size limit of 20 are each written in both the model and the view.**

## Next Steps

### Finishing the MVP (in order)

1. [DONE] Make name matching case-insensitive when cancelling (`Party` constructor and `equals`).
2. [DONE] Reject duplicate parties (`ReservationModel.addParty`).
3. [DONE] Add a maximum party size of 20 (`Party` constructor and `ReservationView.readPartySize`).
4. [DONE] Add the missing `PartyTest` case.

### [IMPLEMENTED] Change feature: indoor and outdoor waitlists

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
| `Section.java` | Tyler | reviewed / revised/ tested |
| `ReservationQueue.java` | Sreenidhi | reviewed / revised / tested |
| `ReservationModel.java` | Sreenidhi | reviewed / revised / tested |
| `ModelDriver.java` | Sreenidhi | wrote / reviewed |
| `ReservationView.java` | Tyler | reviewed / revised / tested |
| `ReservationController.java` | Jas | Wrote / reviewed |
| `PartyTest.java` | Sreenidhi | wrote / reviewed |
| `ReservationQueueTest.java` | Sreenidhi | GenAI draft; reviewed / revised |
| `ReservationModelTest.java` | Sreenidhi | Wrote / reviewed; section tests GenAI-assisted |
| `ReservationViewTest.java` | Tyler | GenAI draft; reviewed / revised |
| `ReservationControllerTest.java` | Jas | wrote / reviewed / revised; `testIndoorOutdoorSession` GenAI-assisted |

### GenAI note

We used Claude for these files and parts:
- **Helped draft for Deliverable 3:**
  - `ReservationView`;
  - `ReservationViewTest`;
  - `ReservationQueueTest`;
  - the controller's Javadoc;
  - the README.
- **Helped revise for Deliverable 4:**
  - `Section`;
  - the section logic in `ReservationModel`, `ReservationView`, `ReservationController`, and `ModelDriver`;
  - the new section tests;
  - the updates to existing tests;
  - this README's D4 Reflection.

We wrote the original model classes, `PartyTest`, the D4 MVP fixes and their tests, and the first controller tests.