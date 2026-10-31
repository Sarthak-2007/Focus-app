# Scout Finds 4 — Print / Receipt Design

**Best serves: GitHub Issue #10 — Receipt** (the session-finish slip). Secondary use: the parked tear-to-file idea, and the Drawer / filed-receipts surface that receives the slip after filing.

**Hunting ground:** receipt and ticket typography, thermal-printer aesthetics done beautifully, printed-slip product design, boarding-pass / packing-slip craft, monospace document design, tear-off rituals, and apps that use a printed artifact as their emotional payoff.

**Monolith context applied throughout:** the finish slip is a proven, loved surface (❤️ reaction to the batch-7 receipt render). Two known defects in the current slip: (1) it has **no total**, and (2) it prints **peeks as blocks**. A **tear-to-file** idea is parked. Every find below is judged against those three facts first.

---

## 1. BERG — Little Printer (2011–2012)

**Sources:**
- https://www.fastcompany.com/1665514/c_fit
- https://www.dezeen.com/2012/03/20/designed-in-hackney-little-printerby-berg/
- https://www.zdnet.com/article/little-printer-is-the-analog-assistant-you-didnt-know-you-wanted/

**What makes it elite:**
Little Printer is still the canonical proof that a narrow thermal strip can carry a whole product. BERG did not print feeds — they invented *publications*: each source was edited for a ~10-inch strip, delivered once or twice a day, topped with a face. CEO Matt Webb's rule, quoted in Fast Company: *"The act of printing — committing to paper — makes a statement, so you want to be sure that what you print is important"* and the studio concentrated on *"density of information or delight."* Paper, in their framing, is *"a screen that never turns off — you can stick it to the fridge, tuck it in your wallet, scribble on it, tear it and give it to a friend."* The hackday reports add the physical detail that mattered: pressing the button and *tearing the paper off on the metal cutting edge didn't get old even after a day of non-stop printing.*

**What applies to Monolith — concretely:**
- **Edit, don't log-dump.** The slip must be a *publication*, not a raw event transcript. One curated delivery per finish: masthead, summary, itemised lines, total, seal — nothing else earns paper. If a line wouldn't be worth printing in ink, cut it.
- **Density OR delight, every section.** Each block of the slip must pass one of BERG's two tests. Blocked-app lines = density. The closing line / seal face = delight. Anything that is neither (timestamps repeated three ways, internal IDs in full) goes.
- **Design for the after-life.** Little Printer slips lived on fridges and in wallets. Monolith's equivalent is the Drawer: design the slip's proportions, header and total so it still reads perfectly as a filed card weeks later, not only in the 5 seconds after finish.

---

## 2. Susie Lu — *Re-viz-iting the Receipt* (2019)

**Sources:**
- https://susielu.com/data-viz/reviziting-the-receipt
- https://www.fastcompany.com/90347782/the-humble-receipt-gets-a-brilliant-redesign
- https://www.core77.com/posts/88647/Is-It-Time-to-Redesign-the-Receipt

**What makes it elite:**
A Netflix data-visualisation engineer rebuilt a real grocery receipt on a real (narrow, black-only) thermal printer, under the printer's actual constraints. Structure: a **summary visualisation at the very top** (bubble chart by category, percentages labelled), then the itemised list **grouped by category**, each category carrying its **own subtotal and % of the bill**, each item carrying a bar scaled to the most expensive item — a shared axis, so lengths are honestly comparable. Her post-project notes are the sharpest part: the strongest critique she accepted was that **the total price needed a larger font size — *"that value should stand out more as you scan the receipt."*** She also discovered the thermal printer *couldn't print solid horizontal lines*, so rules had to become patterns.

**What applies to Monolith — concretely:**
- **This is the direct fix for both known defects.** Missing total → total becomes the largest type on the slip, full stop. Peeks-as-blocks → stop printing peeks as equal block rows; **group them**: `PEEKS — 3 — 04:30 TOTAL` as a category with a subtotal, individual peeks as small lines underneath only if they differ.
- **Summary before items.** Put a tiny top summary — e.g. proportion bars for HELD / PEEKED / BLOCKED-ATTEMPTS — *above* the line items. The user grasps the session in one glance, then audits details below. Exactly Lu's bubble-then-list order.
- **One scale, honestly labelled.** If bars appear, scale every bar to the session's longest component and say so. No decorative bars with incomparable scales.
- **Pattern, not solid rules.** Dotted / dashed separators are not a style affectation — they are what thermal printing can actually do. That is why they read as *printed* and solid black bars read as *UI pretending*.

---

## 3. Peter Smart — Boarding Pass Redesign (2014)

**Sources:**
- https://www.core77.com/posts/26366/Peter-Smarts-Better-Boarding-Pass-Redesign?utm_source=core77&utm_medium=from_tile_image
- https://pointmetotheplane.boardingarea.com/the-boarding-pass-of-the-future-is-vertical-see-what-it-looks-like-here/

**What makes it elite:**
Smart's insight, as Core77 puts it: a boarding pass *"contains multiple bite-sized chunks of information, and you don't need them all at once. But you do need them in a particular order"* — gate before seat, because that's the order you act in. He turned the pass **vertical**, reducing it to *"a series of easy-to-read lines, like the way a movie script reduces the width of dialogue blocks,"* sized it to fit a passport, and left the crucial data visible at the top even while tucked away. The classic pass failed because it was a data dump ordered for the printer, not for the human at a moment of stress.

**What applies to Monolith — concretely:**
- **Order the slip by the finisher's questions, in time order:** 1) *How long did I hold?* → **TOTAL**, huge, first. 2) *Did it hold?* → verdict line (COMPLETED / HELD, breaks used). 3) *What happened inside?* → itemised lines. 4) *Proof* → session number, date/time, seal/barcode at the foot. Anything answering a later question never outranks an earlier one.
- **Keep the vertical discipline.** The slip is a narrow column — do not let the reskin widen it into a card with side-by-side panels. The narrowness *is* the readability, and it's why the current slip feels printed rather than app-like.
- **Design the stub logic like a pass:** information needed *after* filing (total, date, verdict) belongs on the part that stays visible / is kept; session minutiae belong on the part that gets torn away or collapsed. See Find 6.

---

## 4. Receiptify — Michelle Liu (2020– )

**Sources:**
- https://spotifmania.com/spotify-receiptify-how-to-make-a-spotify-receipt/
- http://en.androidguias.com/receiptify/

**What makes it elite:**
Receiptify turns Spotify listening history into a shopping receipt — and went massively viral, repeatedly, for years, with zero marketing. The anatomy is pure receipt vernacular executed completely: custom header, username, **fictitious order number**, itemised top tracks with durations as the "prices", a **TOTAL section that sums the playtime**, a fake card number, and *"Thank you for visiting!"* — finished with a wrinkled-paper texture. It proved the receipt is not a data format people tolerate; it's an *identity artifact people voluntarily share* — because a receipt says "this really happened, here is the proof, itemised."

**What applies to Monolith — concretely:**
- **Steal the full vernacular, not half of it.** Monolith's slip should carry: a session/order number (`No. 0042`), the "customer" (the user), the "cashier" (MONOLITH), line items with durations right-aligned as prices, a ruled-off **TOTAL**, and a closing thank-you / verdict line. Half-vernacular (mono font but no number, no total, no closing line) is why slips feel like screenshots.
- **The total is the shareable punchline.** In Receiptify the summed playtime is the number people screenshot. In Monolith the total focused time is that number — which makes the current missing total not a minor omission but the missing point of the artifact.
- **Wrinkle/texture with restraint:** a faint paper grain beats flat white. Receiptify's crumple is playful; Monolith's should be quieter — grain + serrated edge, not a comedy crease. (Physics details in Find 7.)

---

## 5. Design Receipt Project — Japan (Hiroaki Sato / GRAPH, 2013)

**Sources:**
- https://thebridge.jp/en/2013/09/design-receipt-project

**What makes it elite:**
Japanese convenience stores keep a bin at the register because customers throw receipts away unread. Sato's project attacked exactly that: 16+ designers, including Issei Kitagawa of GRAPH, printed art on **the blank back of the receipt** and sold the rolls as a product. The premise is the lesson: *"In the digital age, receipts are one of the few physical things we all still come in contact with — but no one pays attention to the empty white space on the back."* They turned a throwaway proof into a keepable communication medium without touching the auditable front.

**What applies to Monolith — concretely:**
- **Give the slip a back.** The front stays stern, monospace, auditable — totals, lines, seal. The *reverse* (flip gesture, or the slip's expanded/filed state in the Drawer) is where delight lives: a pattern in the current look's palette, a single line worth keeping, or the next session's stated intention. Front = proof, back = keepsake. Never mix the two jobs on one face.
- **Keepsake is a retention mechanic.** People bin receipts whose only value was proof; they keep ones with a beautiful back. A filed Monolith slip that is beautiful on its back is a Drawer the user actually reopens — which is precisely the quiet, no-scores drawer behaviour the app wants.
- **Rotating backs, fixed front.** Like DRP's designer series, the back can vary by look/season while the front's typography never changes — variety without breaking the receipt's recognisability or truth.

---

## 6. Tear Ticket — Framer component (tear-off physics, specified)

**Sources:**
- https://www.framer.com/marketplace/components/tear-ticket/

**What makes it elite:**
The most completely *specified* digital tear in a shipping component. Its behaviour sheet reads like a physics brief: *"Pull the stub and the paper stretches first, then the tear runs down the perforation with a soft crackle and loose fibres at the edge. Once it comes free, an ink stamp presses onto the ticket."* Around that: two paper stocks for body vs stub, paper grain, a pressed edition number, barcode, configurable perforation/notches, tear distance and tilt, **synthesised sound (no audio files)**, haptics, frames that run only while something moves, **Enter key tears and reattaches**, and full **reduced-motion respect**.

**What applies to Monolith — concretely (this is the spec for the parked tear-to-file):**
- **Sequence matters: stretch → tear runs → stamp lands.** The stamp (FILED / HELD) must press *after* the stub separates, never before — the stamp is the consequence of filing, not a decoration on an intact slip.
- **Gesture:** drag the slip downward from the perforation line; resistance/stretch first, then the tear propagates along the dotted perforation. Bottom stub (session number + total + date — the boarding-pass stub data from Find 3) tears away into the Drawer; the main body stays on screen briefly, stamped.
- **Sound + haptics are synthesised and motion-scoped** — a short crackle only while the tear runs, one haptic tick at separation, one at the stamp. No looping, no audio assets.
- **Accessibility is part of the ritual, not an afterthought:** a tap/Enter equivalent performs the same tear, and reduced-motion users get an instant separation + stamp with no drag physics. If tear-to-file can't meet that bar, it doesn't ship.

---

## 7. The Real Thermal Canon — Font A / Font B, OCR-B, and Heat Physics

**Sources:**
- https://www.hprt.com/Product/POS-PRINTERS/Receipt-Font-Guide.html
- https://stoatworks-labs.com/guides/receipt.pdf
- https://vayce.app/tools/thermal-printer-effect/

**What makes it elite:**
Real receipt typography is a solved, constrained system — and its constraints are exactly what makes a digital slip feel *true* instead of clip-arty:
- **Font A / Font B** are the built-in thermal faces: Font A, large, for store name, receipt number and **total — the things a customer must spot instantly**; Font B, smaller and compact, for long itemised lists. Two sizes, strict jobs.
- **OCR-B** is the ticketing/banking standard — monospaced, tabular, machine- and human-readable; numbers align in columns because every glyph takes the same width, so prices/times line up on the colon and the unit.
- **Heat physics** (Stoatworks' *Receipt* guide simulates a printer faithfully rather than overlaying a "look"): a line of heaters at **8 dots per millimetre**, fired row by row — producing dither, thickened lines, faint tails below marks, and pale banding in large dark areas. Image tools like Vayce's use **Atkinson dithering** (the classic Mac 1-bit algorithm) for the same reason: pure black/white, no grey.

**What applies to Monolith — concretely:**
- **Two type sizes, strict jobs — spec it:** Font-A role = slip header (MONOLITH), session number, and **TOTAL**. Font-B role = every itemised line. Nothing else gets a third size except the verdict stamp. This alone will make the slip read as printed.
- **Tabular mono for all numbers, dotted leaders for alignment:** `INSTAGRAM ......... BLOCKED ×4`, times right-aligned on a fixed column. Dotted leaders are the receipt's native table — they guide the eye across a narrow column with no boxes and no rules.
- **Peeks-as-blocks is physically wrong, not just ugly:** solid blocks are precisely what real thermal heads band and bleed on. Replace block fills with leaders + counts (Find 2). If a filled area is ever needed, render it as dither/pattern, never flat black.
- **Authenticity budget:** pure black on paper-white (threshold, no grey anti-alias mush at small sizes), faint banding/grain, serrated bottom edge. Skip fake coffee stains and heavy crumples — real receipts from a good printer are *clean*; their beauty is precision, not distress.

---

## Druck's synthesis — the slip, fixed in one pass

1. **Add the TOTAL, largest type on the slip** (Finds 2, 4, 7) — ruled off above and below with dotted lines, Font-A role.
2. **Reorder by the finisher's questions** (Find 3): total → verdict → grouped items → proof (number, times, seal/barcode).
3. **Group peeks and blocks into categories with subtotals** (Find 2); dotted leaders + counts instead of filled blocks (Find 7).
4. **Complete the vernacular** (Find 4): session number, customer/cashier lines, thank-you/verdict closing line.
5. **Summary strip at the top** (Find 2): HELD / PEEKED / BLOCKED proportion, one line, before the items.
6. **Give it a back** (Find 5): delight/pattern/intention on the reverse or filed state; front stays auditable.
7. **Park tear-to-file with its physics spec attached** (Find 6): stretch → tear → stamp, synthesised crackle, haptics, keyboard + reduced-motion equivalents.

— Druck (Scout 4)
