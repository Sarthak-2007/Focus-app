# Issue #17 — Preservation, Snapshot #3: a better shape, priced (INNOVATOR, Anker cell)

Prepared: Fri 2026-10-30 ~03:20 IST. **Design + pricing only — nothing implemented, nothing copied.**
Companion: `planner.md` in this folder (staged local run, defects D1–D9). This file answers the
planner's D7 — *A and B are the same disk* — by designing the off-box shape.

---

## 0. First, correct the size premise — the 5.9 GB is mostly not worth preserving

The brief's "~5.9 GB" is the **whole `clone-inspect/` workspace**. Measured on disk today:

| Component | Size | Preserve? |
|---|---|---|
| `tools/` (toolchain) | 3.3 GB | **No** — re-downloadable |
| `trunk-guh/` (Gradle user home) | 2.0 GB | **No** — regenerable, excluded by the snapshot spec already |
| `builds/` (lane sources + issue cells) | 1.1 GB | Yes — this is the payload core |
| `Focus-app/` trunk, APKs, records, refs | ~0.15 GB | Yes |

Actual snapshot payloads, from their own `SNAPSHOT.md` files: **#1 = 569 MB, #2 = 974 MB raw**;
planner's estimate for **#3 = ~520 MB as-is, ~330 MB with lane `.git` dirs pruned** (each lane `.git`
is a clone of a repo already on GitHub — ~187 MB of duplicated shared history).

Every option below is therefore priced **twice**: *Real* (the ~0.5 GB payload) and *Brief-literal*
(5.9 GB raw, if someone insists on archiving the toolchain too). The conclusion is the same either
way; only the chunk counts change.

**Measured compression expectations** (not folklore): a sample of 80 source files from `builds/`
(3.14 MB of `.kt`/`.md`/`.xml`) compressed to **0.39× with gzip-6**; zstd at default levels is
typically equal or better on text. APKs, JARs and Gradle-cache objects are already compressed
(~0.95–1.0×). Mixed-payload estimates used below: Real payload → **~0.3 GB** (with `.git`),
**~0.15–0.18 GB** (`.git` pruned); Brief-literal 5.9 GB → **~5.0 GB** (binary/cache-dominated).

**Measured dedupe (the finding that drives the ranking):** unioning the sha256 manifests of #1 and
#2 — 9,324 entries, 1,543 MB summed — yields only **2,771 unique hashes, 492 MB of unique bytes:
68.1% of the stored bytes are duplicates** (every lane re-copies the same trunk, wrapper and
library files). Any design that stores snapshots as independent blobs pays for the same content
three times. A content-addressed design pays once.

**Transfer-time formula** (box egress speed is not known; do not trust any single row):
time = size × 8 ÷ bandwidth. 0.3 GB → 4 min @10 Mbps, 1.6 min @25, 24 s @100.
5.0 GB → 67 min @10 Mbps, 27 min @25, 6.7 min @100.

---

## 1. Option A — Snapshot set as GitHub Release assets

**Shape:** per snapshot, one release (e.g. tag `snapshot-3`) on a **dedicated archive repo**
(`Sarthak-2007/Focus-archive`, private) — *not* on Focus-app, so a repo accident, a bad force-push
or a future repo transfer can't take the working repo and its archive down in the same gesture.
Payload streamed into `tar` → compressed → split into volumes; `MANIFEST.sha256` (already produced
by the existing ceremony) plus a per-volume hash file committed to Focus-app `main` under
`studio/preservation/` (small, text) and pasted into the release notes.

**Checked constraints** (GitHub Docs, *About releases*, fetched 2026-10-29):
- **Each release asset must be under 2 GiB; up to 1,000 assets per release; "there is no limit on
  the total size of a release, nor bandwidth usage."** Release assets do **not** count toward
  repository size.
- Source: https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases

**Chunking:** use **1 GiB volumes** (half the limit — margin for GiB/GB confusion, and a corrupt
volume costs a 1 GiB re-download, not 2). Real payload: **1 volume**. Brief-literal: **5 volumes**.
Use `split -b 1000m` on a single compressed stream *or* independent per-directory tarballs
(trunk / lanes / cells / apks) — the latter is better: a restore of just the trunk doesn't
download the lanes, and one bad volume doesn't orphan a byte-stream that spans all volumes.

**Cost:** **$0 / ₹0**, forever, at any of these sizes — no storage or bandwidth meter exists for
release assets. Effort: ~30–45 min to script once (`tar` + `split` + `sha256sum` + `gh release
create/upload` — `gh` is already installed on this box; note `zstd` is **not**, so use gzip or
install zstd), then ~10 min per snapshot plus upload time (table in §0).

**Protects against:** box/VM death ✔ (genuinely off-device); silent corruption ✔ *if verified* —
download, `sha256sum -c` per volume against the manifest committed to git, exactly the S5 ceremony
the snapshots already use; accidental deletion on the box ✔.
**Does NOT protect against:** **GitHub account loss** — same account as the working repo, so a
locked/lost `Sarthak-2007` account (password + 2FA) kills repo and archive together; repo deletion
(mitigated, not eliminated, by the separate archive repo); GitHub-the-company failure or a
fair-use/abuse judgement — the docs state no total limit, but Releases are documented as a
*software distribution* mechanism; warehousing multi-GB archives is tolerated-by-silence, not
contractually promised. Private-repo assets also need an authenticated session/PAT to download —
on a fresh box, account recovery comes *before* restore.

**Restore procedure:** sign in → `gh release download snapshot-3 -R Sarthak-2007/Focus-archive` →
`sha256sum -c` → concatenate/untar per directory → run the planner's §8 independent restore test.

**Failure modes:** interrupted upload leaves a partial asset set (detectable only via the manifest
— never trust the asset list); a single flipped bit in a stream-split volume invalidates everything
after it (why per-directory tarballs are specified); release/tag deleted by anyone with write
access — there is no immutability guarantee worth relying on.

---

## 2. Option B — Archive / orphan branch carrying the snapshot

**Shape:** orphan branch (`archive/snapshot-3`) in Focus-app or a dedicated repo, holding either
the snapshot tree or `git bundle` / tar chunks as blobs.

**Checked constraints** (GitHub Docs, *About large files on GitHub*, fetched 2026-10-29):
- Files **>50 MiB warn, >100 MiB are blocked** in normal git. Browser uploads cap at 25 MiB.
- **"We recommend repositories remain small, ideally less than 1 GB, and less than 5 GB is
  strongly recommended"**; oversize repos can draw a GitHub Support corrective-action email.
  Git is explicitly "not designed to serve as a backup tool" (same page).
- Source: https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-large-files-on-github

**Pricing it honestly:** the tar must be sawn into **≤95 MiB chunks — ~4 chunks for the Real
payload, ~53 for Brief-literal** — and *every snapshot adds permanently to repo size*: orphan
branches share the repo's object store, deleting the branch does not reclaim space on GitHub's
side, and history is append-only in practice. Three snapshots ≈ 1.5 GB (Real, uncompressed-in-git;
git's own zlib gets the text ratio but not the APKs) — already past the 1 GB "ideal", heading at
the 5 GB line with #4, #5… Worse, if the branch lives in Focus-app, **every default clone pays
for it**: this studio runs 16+ issue cells, each cloning that repo — the archive would tax every
future cell, CI run and fresh lane by gigabytes, forever, to preserve data that changes never.
A dedicated archive repo contains that tax but keeps every other defect.

**Cost:** $0 money; the highest effort and friction of any option (chunk bookkeeping, slow clones,
`git-sizer` hygiene, support-email risk). **Protects against:** box death ✔; corruption ~ (git's
object hashing detects it, eventually, on fsck/clone); account loss ✘ (same account).
**Restore:** clone the archive repo (full history, all snapshots, whether you want them or not) →
checkout branch → reassemble chunks → verify manifest.
**Failure modes:** one missing/corrupt chunk = whole stream dead; push rejections at the 100 MiB
boundary from any tool that repacks a chunk slightly over; repo becomes uncloneable-slow long
before it becomes impossible — death by friction. **Verdict: the manifest belongs on a branch.
The bulk never does.**

---

## 3. Option C — Git LFS, and off-box object stores

### C1. GitHub Git LFS (in a dedicated archive repo)

**Checked constraints** (GitHub Docs, *About storage and bandwidth usage* for Git LFS, fetched
2026-10-29 — German locale served; figures cross-checked against secondary sources below):
- Free/Pro plans include **10 GiB storage + 10 GiB/month bandwidth**; Team/Enterprise 250 GiB each.
- The old prepaid "data packs" model has been **replaced by metered billing**: overage at
  **$0.07/GiB-month storage and $0.0875/GiB bandwidth** (rates via Stanford's Cardinal Cloud
  GitHub page and multiple secondary sources, e.g. https://uit.stanford.edu/service/github —
  GitHub's own billing page would not fetch directly in this session, so treat the metered rates
  as *well-corroborated secondary*, the included quotas as *official*).
- Per-file LFS limit on Free/Pro is **2 GiB** (same chunking as Option A); each new version of an
  LFS file bills its *full size again* in storage; every download — including CI/Actions checkouts
  and every restore — bills bandwidth to the repo owner. If quota is exceeded with no payment
  method/budget, **LFS downloads are disabled until the next month** and clones return pointer
  files instead of content.
- Source: https://docs.github.com/de/repositories/working-with-files/managing-large-files/about-storage-and-bandwidth-usage

**Price:** Real payload, all 3 snapshots, no dedupe (~1.2 GB compressed stored): **$0** — inside
the 10 GiB included storage, and a full restore (~1.2 GiB) fits the monthly bandwidth quota if
nothing else used it. Brief-literal cumulative (~15 GB): ~5 GiB over → **~$0.35/mo (~₹33/mo)**
storage, plus ~$0.44 per full-restore month. Cheap — but the *failure mode is the price*: a
restore attempted in a month when CI already ate the bandwidth returns **pointer files**, i.e.
the backup silently isn't there on the worst day, unless a payment method is on file.

### C2. S3-compatible object store (Cloudflare R2 / Backblaze B2) — plain chunks

**Checked prices:**
- **Cloudflare R2** (official pricing page, fetched 2026-10-29): Standard storage
  **$0.015/GB-month, egress free**, free tier **10 GB-month/month** + 1M Class A / 10M Class B ops.
  https://developers.cloudflare.com/r2/pricing/
- **Backblaze B2** (secondary sources quoting Backblaze's published rate, Oct 2026):
  **$6.95/TB-month (~$0.00695/GB)**, first 10 GB free, egress free up to 3× stored volume, then
  $0.01/GB. Official page not fetched this session — verify before purchase.

**Price at our sizes:** storing all three snapshots as plain compressed chunks (~1.2 GB): **$0/mo**
on either (inside free tiers). Even Brief-literal cumulative (~15 GB): R2 ≈ **$0.08/mo (~₹7/mo)**;
B2 ≈ $0.03/mo. A full restore costs **$0 egress** on R2, $0 on B2 within 3×. For contrast, AWS S3
Standard ($0.023/GB-mo + $0.09/GB egress, secondary): ~$0.28/mo and ~$0.45 *per restore* — still
trivial money, strictly worse terms.
**New cost C1 doesn't have:** a *separate account + payment card + API credentials* to create and
guard. That separation is also the entire point (see §5).

**Protects against:** box death ✔; corruption ✔ with the same sha256 ceremony; **GitHub account
loss ✔** (different provider, different credentials — the only options here that break the
single-account correlation). **Restore:** `rclone`/`aws s3 cp` the chunks → verify → untar.
**Failure modes:** lost API keys/credentials = lost archive (same class of risk as a lost
password anywhere — mitigate in §4); bucket accidentally made public = leak of a private codebase
(set private + Block Public Access equivalents at creation, verify once); provider account
closure for non-payment if the card expires — at $0–0.08/mo the realistic version of this is
*forgetting the account exists*, which the in-repo manifest (§4) guards against by recording
where the bulk lives.

---

## 4. Option D (invention) — "Manifest in git, chunks addressed by hash, bulk in two cheap stores"

**The insight:** the snapshot ceremony *already produces the hard part* — `MANIFEST.sha256`,
a per-file content index. #1+#2 prove 68% of stored bytes are duplicates. So stop archiving
*snapshots*; archive *content, once*, and let each snapshot be a small text manifest that
references it.

**Shape:**
1. **Content store:** run **restic** (single static binary; content-defined chunking, dedupe
   across all snapshots, zstd compression, AES encryption always on, native S3/B2/R2 backends,
   `restic check` for integrity) with the snapshot payload tree as its input. Expected repo size
   for #1+#2+#3 together: unique raw bytes 492 MB (#1+#2 measured) + #3's genuinely new files,
   compressed → **~0.3–0.45 GB total** (estimate from §0 ratios; restic's chunk-level dedupe
   should beat the file-level 68% figure, since edited files share most chunks). Every future
   snapshot uploads **only its delta** — on the #1→#2 pattern, tens of MB, minutes at worst.
2. **Two backends, same restic repo format:** primary **R2**, secondary **B2** (`restic copy`
   between repos, or a Release-assets mirror of the packed repo per Option A as the secondary —
   see ranking note). Two providers, two credential sets: no single account loss, provider
   failure, or quota lockout takes the archive.
3. **The index lives in git:** commit to Focus-app `main`, per snapshot, under
   `studio/preservation/<name>/`: `SNAPSHOT.md`, `MANIFEST.sha256`, the restic snapshot ID,
   backend names (not credentials), and restore instructions. ~100 KB of text. Anyone who clones
   the repo can *prove what should exist* and detect a lying/incomplete backend — the manifest
   is the audit, the stores are just shelves. This also fixes the discovery failure mode: the
   archive can never be "forgotten", because its map is in the repo everyone already has.
4. **Restore drill, scheduled not aspirational:** each snapshot's ceremony ends with the existing
   S5 restore test pointed at the *remote* repo (restore to scratch from R2, `sha256sum -c`
   against the committed manifest) — the same PASS line, now proving an off-box restore.

**Cost:** storage **$0/mo** at current size on both backends (0.3–0.45 GB ≪ 10 GB free tiers);
at 20 GB stored it is ~$0.15/mo (~₹14) on R2 and ~$0.07/mo on B2; at 100 GB, ~$1.35 + $0.63/mo
(~₹188/mo combined). Effort: **2–3 hours one-time** (install restic — not present on this box
today; init two repos; write the wrapper that runs the existing ceremony → restic backup →
commit index), then **~10 min per snapshot**, mostly unattended delta upload. No new GitHub
limits are touched at all.

**Protects against:** box death ✔; **account loss ✔** (GitHub loss leaves both stores; one
store's loss leaves the other + the manifest); silent corruption ✔✔ — the strongest of any
option: content addressing means a corrupted chunk fails its hash on `restic check`/restore,
the manifest gives a second, independent, in-git verification layer, and dedupe means corruption
surface is stored once, not three times; ransomware/wiper on the box ✔ (encrypted, append-only
habits possible: restic repos tolerate `--append-only` style restricted keys on S3 backends).
**Failure modes — stated plainly:** **lose the restic password = lose everything** (encryption
is real). Mitigation is procedural, not technical: password in the Secure Vault *and* one
offline copy held by Sarthak personally; never in git, never in the manifest. Credential sprawl
(two store accounts + restic password) is the ongoing tax — three secrets, inventoried in the
committed index by name/location, not value. Restic itself is a dependency: pin the version in
the index; its repo format is documented and the binary is a single file that can itself be
attached to a GitHub release for bootstrapping a restore on a bare machine.

---

## 5. Comparison

| | A: Release assets | B: Archive branch | C1: Git LFS | C2: R2/B2 chunks | **D: restic + manifest-in-git** |
|---|---|---|---|---|---|
| Money (real, cumulative) | **$0** | $0 | $0 (quota) / paise over | **$0** (free tiers) | **$0** (free tiers) |
| Money (brief-literal, cum.) | **$0** | $0 | ~₹33/mo + per-restore | ~₹7/mo | ~₹7–14/mo (still deduped) |
| Box death | ✔ | ✔ | ✔ | ✔ | ✔ |
| GitHub account loss | ✘ | ✘ | ✘ | ✔ | **✔ (two providers)** |
| Silent corruption | ✔ w/ manifest | ~ | ~ | ✔ w/ manifest | **✔✔ (hash-addressed + manifest)** |
| Clone tax on studio | none | **severe, permanent** | pointer tax on clones | none | none |
| Quota-lockout-on-restore-day | none | none | **yes, if bandwidth spent** | none | none |
| Setup effort | 30–45 min | 1–2 h + forever friction | 30 min | 45–60 min | 2–3 h once |
| Per-snapshot effort | ~10 min + full upload | high | ~10 min + full upload | ~10 min + full upload | **~10 min, delta only** |
| Restore time (real payload) | minutes | long (full clone) | minutes *if quota* | minutes | minutes |

FX: ₹95.2 / $1 (Wise mid-market, 2026-10-29).

## 6. Ranked recommendation

1. **Option D — restic to R2 (primary) + B2 (secondary), manifest committed to Focus-app.**
   **Price of the top pick: ₹0/month at today's size** (projected stored ~0.3–0.45 GB vs 10 GB
   free on each backend), **₹0 per restore** (free egress both), one-time 2–3 h of Ren's time,
   and a hard ceiling anyone can sanity-check: even at 100 GB stored it stays under ~₹190/month.
   It is the only design that survives *every* named threat — box death, GitHub account loss,
   and silent corruption — and the only one whose cost per future snapshot *falls* (deltas)
   instead of growing linearly forever.
2. **Option A — Release assets on a dedicated `Focus-archive` repo.** Also ₹0, available
   *tonight* with tools already installed, and the right **interim step**: ship Snapshot #3's
   single compressed volume as a release asset the same day the local run passes, before D's
   setup exists — and afterwards keep it as D's GitHub-side tertiary copy if wanted. Its ceiling
   is trust: same account, distribution-mechanism tolerance, no dedupe (each snapshot re-uploads
   in full).
3. **Option C2 standalone (plain chunks to R2)** if restic is rejected as a dependency: nearly
   as safe as D against loss, weaker on corruption detection discipline and per-snapshot cost.
4. **Option C1 — Git LFS.** Works and is nearly free, but the quota-lockout restore failure and
   full-size-per-version billing make it a backup that can decline to restore. Not recommended
   as the primary.
5. **Option B — archive branch.** Rejected. It permanently taxes every clone in a studio that
   clones constantly, breaches GitHub's stated repo-size guidance within a few snapshots, and
   cannot be un-done. Only its manifest half survives — inside Option D.

**Sequencing for Ren (decision, not implementation):** A this week for #3 (one asset, ~0.3 GB,
₹0) → D as the standing shape from #4 onward, backfilling #1–#3 into the restic repo (0.49 GB of
unique content — one short upload) → B never, C1 never.

---

### Sources actually checked (2026-10-29/30)
- GitHub Docs, *About releases* — 2 GiB/asset, 1,000 assets/release, no total-size or bandwidth
  limit: https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases
- GitHub Docs, *About large files on GitHub* — 50 MiB warn / 100 MiB block / 25 MiB browser;
  repos ideally <1 GB, strongly <5 GB; git "not designed to serve as a backup tool":
  https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-large-files-on-github
- GitHub Docs, *About storage and bandwidth usage* (Git LFS) — 10 GiB + 10 GiB included on
  Free/Pro, 250 GiB on Team/Enterprise; metered billing replaced data packs; quota-exceeded
  behaviour (German locale served by docs.github.com):
  https://docs.github.com/de/repositories/working-with-files/managing-large-files/about-storage-and-bandwidth-usage
- LFS metered overage rates $0.07/GiB storage, $0.0875/GiB bandwidth — secondary corroboration:
  https://uit.stanford.edu/service/github
- Cloudflare R2 official pricing — $0.015/GB-month Standard, egress free, 10 GB-month free tier:
  https://developers.cloudflare.com/r2/pricing/
- Backblaze B2 $6.95/TB-month, egress free to 3× stored — secondary sources quoting Backblaze's
  published rate (official page not fetched this session; verify before purchase).
- Local measurements: snapshot sizes/manifests in `~/workspace/preservation/monolith-snapshots/`
  (#1: 568,973,290 B / 3,325 files; #2: 974,007,921 B / 5,999 files); dedupe union computed from
  both `MANIFEST.sha256` files (2,771 unique hashes / 491,734,892 unique bytes); gzip-6 sample
  ratio 0.39 on 80 source files from `builds/`; workspace composition via `du` (tools 3.3 GB,
  trunk-guh 2.0 GB, builds 1.1 GB).
