import re
import subprocess
import sys

# STURM3-11 mergebot resolver for agent/issue-113 vs current main.
# Every rule is pattern-matched to a conflict segment STURM3-11 read
# and decided by hand (see mergebot/status-113.txt history, issue
# #113). Any segment matching NO rule aborts the merge — nothing is
# guessed.
#
# Round-1 rules:
#  R1 both-added receipt fields/args (penalty + forged at one anchor).
#  R2 anchor declaration (nowWall kept + nullable boot probe).
#  R3 finish controller: #113's arm retires the >10-min discard
#     (subsumes R1 #40's sleep exemption; #40's discard retired).
#  R4 sentence when: Jammed+Proving from main, running arm from #113.
#  R5 re-anchor block: #113 comment + newStartWall, main's guards.
# Round-2 rules (main moved again):
#  R6 both-added single-line assignments at one anchor (Repository
#     receipt assembly: penaltySecondsAdded + openExposureApps).
#  R7 receipt assembly overlap: our penalty+forged vs their
#     forged+heldShut+heldPeek — emit penalty, forged, heldShut,
#     heldPeek (commas between).
#  R8 ReceiptState fields: our penaltySecondsAdded vs their
#     openExposureApps (#129 F3) — keep both.
#  R9 ReceiptSheet parallel if-blocks (penalty line + open-exposure
#     line share one Text continuation): emit our block, the shared
#     continuation, then their block; the file's own continuation
#     completes theirs.
#  R10 computeVerifiedSessionElapsed reboot branch: take MAIN's
#     #81 F3 form. #113 F5's absolute "never credit uptime" form is
#     superseded by main's later, deliberate law: uptime of the
#     CURRENT (witnessed) boot credits; an unwitnessed boot span
#     (boot-count delta >= 2, TamperWitness) convicts as tamper.
#     Documented on issue #113; RebootTruthRepositoryTest re-based
#     to the composed doctrine in the same port.

SEG = re.compile(r"<<<<<<< [^\n]*\n(.*?)\n=======\n(.*?)\n>>>>>>> [^\n]*\n", re.S)


def lines(block):
    return block.split("\n")


def with_comma(line):
    return line if line.rstrip().endswith(",") else line + ","


def rule_r7(our, their):
    if (
        "penaltySecondsAdded" in our
        and "forgedArtifactName" in our
        and "heldShutAtSeal" in their
        and "heldPeekAtSeal" in their
    ):
        def grab(block, needle):
            for l in lines(block):
                if needle in l:
                    return l
            return None

        pen = grab(our, "penaltySecondsAdded")
        forg = grab(their, "forgedArtifactName") or grab(our, "forgedArtifactName")
        shut = grab(their, "heldShutAtSeal")
        peek = grab(their, "heldPeekAtSeal")
        if pen and forg and shut and peek:
            return "\n".join([with_comma(pen), with_comma(forg), with_comma(shut), peek]) + "\n"
    return None


def rule_r1(our, their):
    if "penaltySecondsAdded" in our and "forgedArtifactName" in their:
        ol = lines(our)
        ol[-1] = with_comma(ol[-1])
        return "\n".join(ol + lines(their)) + "\n"
    return None


def rule_r8(our, their):
    if "val penaltySecondsAdded" in our and "val openExposureApps" in their:
        tl = lines(their)
        tl[-1] = with_comma(tl[-1])
        return our + "\n" + "\n".join(tl) + "\n"
    return None


def rule_r6(our, their):
    ol, tl = lines(our), lines(their)
    if (
        len(ol) == 1
        and len(tl) == 1
        and "penaltySecondsAdded = " in ol[0]
        and " = " in tl[0]
        and "forgedArtifactName" not in ol[0]
        and "forgedArtifactName" not in tl[0]
    ):
        return with_comma(ol[0]) + "\n" + with_comma(tl[0]) + "\n"
    return None


def rule_r9(our, their):
    if "receipt.penaltySecondsAdded > 0" in our and "receipt.openExposureApps.isNotEmpty()" in their:
        continuation = [
            "                        fontFamily = AtkinsonHyperlegibleFamily,",
            "                        fontWeight = FontWeight.Bold,",
            "                        fontSize = 12.sp,",
            "                        color = scheme.onBackground",
            "                    )",
            "                }",
        ]
        return our + "\n" + "\n".join(continuation) + "\n" + their + "\n"
    return None


def rule_r10(our, their):
    if "Pair(priorElapsed, !hmacValid)" in our and "TamperWitness.unwitnessedBoot" in their:
        return their + "\n"
    return None


def rule_r2(our, their):
    if (
        "val nowWall = System.currentTimeMillis()" in our
        and "querySystemBootCount()" in our
        and "querySystemBootCountOrNull()" in their
    ):
        now_wall = [l for l in lines(our) if "val nowWall" in l][0]
        return now_wall + "\n" + their + "\n"
    return None


def rule_r3(our, their):
    if (
        "else -> onFinishE0(row, kind, witnessed = false)" in our
        and "handledFinishIds += row.id" in their
    ):
        return our + "\n"
    return None


def rule_r4(our, their):
    if "ArmPhase.Proving -> null" in our and "provingSentenceFor(armPhase)" in their:
        def pick(block, needle):
            for l in lines(block):
                if needle in l:
                    return l
            return None

        jammed = pick(their, "ArmPhase.Jammed") or pick(our, "ArmPhase.Jammed")
        proving = pick(their, "ArmPhase.Proving")
        running = pick(our, "isSessionRunning")
        if jammed and proving and running:
            return "\n".join([jammed, proving, running]) + "\n"
    return None


def rule_r5(our, their):
    if "val newStartWall" in our and "currentBoot ?: session.bootCount" in their:
        ol, tl = lines(our), lines(their)

        def idx(block, needle):
            for i, l in enumerate(block):
                if needle in l:
                    return i
            return -1

        def grab(block, needle):
            i = idx(block, needle)
            return block[i] if i >= 0 else None

        i_reboot = idx(ol, "val rebootDetected")
        i_t_mono = idx(tl, "val newStartMono")
        i_t_reboot = idx(tl, "val rebootDetected")
        new_start_wall = grab(ol, "val newStartWall")
        new_boot_count = grab(tl, "val newBootCount")
        if i_reboot < 0 or i_t_mono < 0 or i_t_reboot < 0 or not new_start_wall or not new_boot_count:
            return None
        comment = ol[:i_reboot]
        theirs_head = tl[i_t_reboot : i_t_mono + 1]
        return "\n".join(comment + theirs_head + [new_start_wall, new_boot_count]) + "\n"
    return None


RULES = [rule_r7, rule_r10, rule_r9, rule_r8, rule_r6, rule_r1, rule_r2, rule_r3, rule_r4, rule_r5]

files = subprocess.check_output(
    ["git", "diff", "--name-only", "--diff-filter=U"], text=True
).split()
if not files:
    print("no unmerged files; nothing to resolve")
    sys.exit(1)

for path in files:
    src = open(path).read()
    state = {"n": 0}

    def repl(m):
        our, their = m.group(1), m.group(2)
        for rule in RULES:
            out = rule(our, their)
            if out is not None:
                state["n"] += 1
                return out
        raise ValueError("unknown conflict segment in " + path + ":\nOUR:\n" + our + "\nTHEIR:\n" + their)

    try:
        out = SEG.sub(repl, src)
    except ValueError as e:
        print(e)
        sys.exit(1)
    if state["n"] == 0:
        print("no segments matched in", path)
        sys.exit(1)
    if "<<<<<<<" in out or ">>>>>>>" in out:
        print("conflict markers remain in", path)
        sys.exit(1)
    open(path, "w").write(out)
    print(f"resolved {path}: {state['n']} segment(s)")

print("all resolved:", files)
