# Reservation Master

A console waitlist for a restaurant with separate indoor and outdoor lines, each built on a singly linked FIFO queue. Hosts can add, view, seat, and cancel parties.

## How to compile and run

**In Eclipse:** import the `ReservationMaster` folder as an existing project, and put `student.jar` on the build path (it's only needed for the tests).
- **Program:** run `main.ReservationController`.
- **Model only (no view):** run `main.ModelDriver`.
- **Tests:** right-click the `tests` package and choose Run As → JUnit Test. All 79 tests pass.

**From a terminal** (in the `ReservationMaster` folder):

```
javac -d bin src/main/*.java
java -cp bin main.ReservationController
```

To compile and run a test class from a terminal, add `student.jar` to the classpath:

```
javac -d bin -cp student.jar src/main/*.java src/tests/*.java
```

**How the view connects to the model:** every user action goes through `ReservationController`, which calls `ReservationModel` and then passes the result to `ReservationView`. The view keeps no copy of model state. Its only fields are the `Scanner` and the output stream. Each display is built from data the controller just fetched from the model, such as a fresh `Party[]` from `getWaitlist`, so what the host sees always matches the model's current state.

The program survives the bad input named in our scope:
- a non-number or out-of-range menu choice;
- a blank name, or a name with digits or symbols;
- a party size that is not a number, or is zero, negative, or above 20;
- a section answer other than I, O, or E (or E when choosing a line to seat from).

In each case it re-prompts and shows an example of valid input.

## Reflection

In Deliverable 4 we first finished the four MVP fixes, then added our change feature: **separate indoor and outdoor waitlists**. A party chooses indoor, outdoor, or either. "Either" joins the shorter line, with ties going to indoor. The host seats and views the next party by section, sees both lines and both counts at once, and can cancel a party without knowing which line it is in.

### How did our design absorb the change?

**Classes the change feature actually touched, compared with our D3 prediction:**

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
  - Added tests not in the plan:
    - interleaved adds and seats, in both `ReservationQueueTest` and `ReservationModelTest`, to check FIFO order when the front and rear move past each other;
    - a controller test for the new bad input the feature introduces: a duplicate in the other section, an empty section, and "either" when seating.

### What design change would make the next feature cheaper?

The cost of this feature came from places where "the list of sections" is spelled out by hand. The model has two named fields (`indoor`, `outdoor`) and `queueFor` picks between them with `if`s. The view's `readSection` and `sectionName` list the answers and labels for each value. Adding a third section, such as a patio, would mean editing all of those.

The change that would make that cheaper is to store the queues in a map keyed by section (`EnumMap<Section, ReservationQueue>`). The display label and input letter would live on the enum itself, for example `Section.PATIO("Patio", 'P')`. Then a new section would be one new enum value. The model would loop over the map for cancel, duplicates, and "either" routing, and the view would loop over `Section.values()` to build its prompt.

For our likely next feature, estimated wait times, the useful change is different. `ReservationModel` should expose a party's position in its line, so the estimate can be computed in the model from position times minutes per party. The view would only print the number.

### What doesn't work yet?

All MVP features and the indoor/outdoor feature work, and all 79 tests pass. The known gaps:
- **"Either" routing counts parties, not people.** A line with one party of 12 counts as shorter than a line with two parties of 2.
- **A party can't switch sections without losing its place.** It has to cancel and rejoin at the back.
- **Estimated wait times are not implemented.**
- **The cancel message repeats the name as typed,** for example "Reservation for ruiz (5) cancelled."
- **Extra spaces inside a name make a different party** ("Mary  Jane" vs. "Mary Jane").
- **The section values, the "either" rule, and the size limit of 20 are each written in both the model and the view.**

## Next Steps

### What's left for the final build (in order)

1. **Make "either" routing count people, not parties,** in `ReservationModel.addParty`, so a party of 12 counts as a longer line than two parties of 2. Add tests for the new tie and size cases.
2. **Make the cancel message use the stored name** (show "Smith" even if the host typed "smith"). `cancelReservation` would need to return the removed `Party` instead of `boolean`, and the controller and tests would be updated to match.
3. **Collapse repeated spaces inside names** in the `Party` constructor, so "Mary  Jane" matches "Mary Jane". Add a `PartyTest` case.
4. **Store the maximum party size in one place.** Add a `Party.MAX_SIZE` constant and have `ReservationView.readPartySize` use it.
5. **Do a final pass:** run all tests in Web-CAT, fix any style or coverage warnings, finish the attribution entries, and rehearse the demo.

### Stretch goals

- **Committed and done:** multiple queues (indoor and outdoor seating), our D4 change feature.
- **Committed, if steps 1 to 5 finish early:** estimated wait times, calculated in the model from a party's position in its line, and shown in the waitlist display.
- **Dropped:**
  - reservation types (scheduled vs. walk-in), because scheduled times don't fit a first-in, first-out line;
  - table availability, because it is a separate system (tables, capacities, matching) that is too large for the time left.

### 3-minute demo

| Time | What we show |
|---|---|
| 0:00–0:20 | What the app is, and the MVC split: model, view, controller. |
| 0:20–0:50 | Run `ModelDriver`: the model runs with no view at all. |
| 0:50–1:30 | Run the program and add three parties: one indoor, one outdoor, and one "either" that joins the shorter line. Then view the waitlist and counts for both sections. |
| 1:30–2:05 | Seat the next outdoor party, cancel an indoor party from the middle of its line, and view the waitlist again to show the order was kept. |
| 2:05–2:35 | Bad input: letters as a party size, size 21, a name with digits, "either" when seating, and a duplicate party. The program re-prompts or shows an error every time, without crashing. |
| 2:35–3:00 | Run the test suite (79 passing), and point out that `Party` and `ReservationQueue` never changed for the new feature. |

### Who does what

| Item | Owner |
|---|---|
| 1. Routing by number of people, and its tests | Sreenidhi |
| 2. Cancel message uses the stored name | Jas (controller) and Sreenidhi (model) |
| 3. Collapsing spaces in names, and its `PartyTest` case | Sreenidhi |
| 4. Single maximum-size constant | Tyler |
| 5. Web-CAT pass and attribution | All |
| Estimated wait times, if attempted | Sreenidhi (model) and Tyler (view) |

## Attribution

<!-- TEAM: replace every [bracketed] entry before submitting. -->

| File | Who | How |
|---|---|---|
| `Party.java` | Sreenidhi | reviewed / revised / tested |
| `Section.java` | Tyler | reviewed / revised / tested |
| `ReservationQueue.java` | Sreenidhi | reviewed / revised / tested |
| `ReservationModel.java` | Sreenidhi | reviewed / revised / tested |
| `ModelDriver.java` | Sreenidhi | wrote / reviewed |
| `ReservationView.java` | Tyler | reviewed / revised / tested |
| `ReservationController.java` | Jas | Wrote / reviewed |
| `PartyTest.java` | Sreenidhi | wrote / reviewed |
| `ReservationQueueTest.java` | Sreenidhi | GenAI draft; reviewed / revised |
| `ReservationModelTest.java` | Sreenidhi | Wrote / reviewed |
| `ReservationViewTest.java` | Tyler | GenAI draft; reviewed / revised |
| `ReservationControllerTest.java` | Jas | wrote / reviewed / revised |

### GenAI note

We used Claude for these files and parts:
- **For Deliverable 3:**
  - `ReservationView`;
  - `ReservationViewTest`;
  - `ReservationQueueTest`;
  - the controller's Javadoc;
  - the README.
- **For Deliverable 4:**
  - `Section`;
  - the section logic in `ReservationModel`, `ReservationView`, `ReservationController`, and `ModelDriver`;
  - the new section tests;
  - the updates to existing tests;
  - this README's D4 Reflection.

We wrote the original model classes, `PartyTest`, the D4 MVP fixes and their tests, and the first controller tests.