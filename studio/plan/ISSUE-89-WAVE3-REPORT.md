# ISSUE #89 — WAVE-3 REPORT · R2 asset-bloat

**Lead:** FLUT (Wave-3B cluster, flat under Ren). Base: origin/main @ 0a00e95.
All numbers re-measured by the lead on the current tree (du / git ls-files /
md5sum), not copied from the audit. The audit proved accurate to the byte
on every claim.

## Per-finding dispositions

### 1. [HIGH] Dead public/ tree — VERIFIED, STANDS-BLOCKED (priced)
Measured: **9,273,474 bytes, 39 tracked files**, grep over kt/kts/xml/gradle
finds **0 code or build references** — the audit's fact holds exactly.
Blocked, not deleted: these 39 files are the design programme's reference
shelf (TE EP-133 / TP-7 product references, the opal-home reference
screenshot) — the raw material the revamp lanes are designing from right
now. Verified there are **no other copies in the repo** (git ls-files name
search for ref_opal_home.png / ref_te_tp7.png / ref_te_ep133.png outside
public/ + the temp cache: 0). Deleting the shelf's only copies mid-revamp,
from an audit branch, would be a silent downgrade of the design work —
Sarthak's ask-first rule. **Price of the eventual prune: 9,273,474 bytes
the day the revamp ships and the shelf is archived deliberately.**

### 2. [HIGH] .tempmediaStorage cache — FIXED (commit dcfdf7c)
Measured before: **2,464,656 bytes, 18 files**, all git-tracked, no
.gitignore entry — duplicates of public/ content in a directory literally
named temp storage. This was pure tool scratch with zero reference value.
Fix: `git rm -r` of the whole directory + `.gitignore` gains
`.aistudio/**/.tempmediaStorage/` so it can never be committed again.
.aistudio total: 2,601,618 → **136,962 bytes**.
Deleted, name by name (all under
`.aistudio/artifacts/brain/126e1213-db14-4552-8d59-57595258f3a4/.tempmediaStorage/`):
media_1793023715955_d0650839.png · media_1793023717498_75f4e3cf.png ·
media_1793023717883_10c5160e.png · media_1793023719671_e8c56246.png ·
media_1793113161735_c3831c00.png · media_1793114213920_c953065b.jpg ·
media_1793114280718_5bc22f85.jpg · media_1793114436520_70e5657b.jpg ·
media_1793114438245_1b3a1340.png · media_1793114498787_7601b3fe.png ·
media_1793114623950_5c3d3269.jpg · media_1793114676388_ddda9400.jpg ·
media_1793114826050_b4295c45.jpg · media_1793114890281_c367dc30.jpg ·
media_1793114981623_4197db65.jpg · media_1793121023933_2d46ae16.jpg ·
media_1793196420247_294a4c2d.pdf · media_1793202206360_7a4e0d1e.pdf.
The 10 remaining .aistudio files are AI Studio markdown documents
(requirements matrix, developer guide, feature catalog) — records, not
scratch; left untouched.

### 3. [MEDIUM] Triple-encoded images — VERIFIED, STANDS-BLOCKED (priced)
Measured exactly as claimed: ref_opal_home.png 2,707,530 beside
ref_opal_home_opt.jpg 22,644; ref_te_ep133 .png 227,807 + .webp 30,838 +
_opt.jpg 11,837; ref_te_tp7 .png 160,424 + .webp 18,060 + _opt.jpg 11,502;
ref_tiimo_focus .png 77,205 + _opt.jpg 8,895; ref_things3_checklists .png
71,955 + _opt.jpg 17,004. Same decision as finding 1, because it is the
same shelf: the .png originals are the only full-fidelity copies (the
_opt derivatives are ~1–8% of the bytes — thumbnails, not equivalents),
and the TP-7/EP-133 references are live design input this week.
**Price: 3,293,819 bytes** recoverable in the same post-revamp shelf
prune as finding 1 — one decision, taken once, by Ren/Sarthak.

### 4. [LOW] Legacy launcher PNGs — VERIFIED, fix REJECTED as downgrade
Measured: **41,314 bytes** across the 10 per-density ic_launcher PNGs;
minSdk is 24, so they are the launcher icon on API 24–25 only.
Sub-finding verified: mipmap-anydpi-v26/ic_launcher.xml and
ic_launcher_round.xml are byte-identical (md5 5058b724…67221 both) —
the "round" icon is undifferentiated; noted, left alone (a design
choice, not bloat worth a risky change).
Deleting the PNGs for 41KB would strip the launcher icon from API
24–25 devices — a user-facing downgrade, forbidden without Sarthak's
approval. The waste dies naturally the day minSdk rises; that is his
call, priced here at 41,314 bytes.

## User-facing changes

NONE. The only deletion is tool scratch that never entered the app;
nothing about how the app works, looks or feels changes.

## Seat signatures (bundle carried by the lead — spawn pool saturated;
## seats covered by FLUT, marked bundle-covered per dispatch law)

- Verifier — every byte count re-measured; audit confirmed exact on all
  four findings, including the identical-XML sub-finding. *bundle-covered*
- Keeper of Record — all 18 deletions named above; nothing unnamed was
  removed, and the surviving shelf is inventoried by the measurements.
  *bundle-covered*
- Brutal critic — the cache was the only true garbage here; the rest of
  the "bloat" is a design library wearing a bloat costume, and the
  report says so instead of deleting for a headline number.
  *bundle-covered*
- Implementation-quality — fix landed only where the fix was pure
  (scratch + gitignore); everything judgement-shaped is priced, not
  forced. *bundle-covered*
- Remaining named seats — no distinct ruling on a repo-hygiene question;
  covered by the lead. *bundle-covered*
