import re
import subprocess
import sys

# STURM3-11 mergebot resolver v2 for agent/issue-113 vs current main.
# Every rule below is pattern-matched to a conflict segment STURM3-11
# read and decided by hand (see mergebot/status-113.txt history and
# issue #113). Any segment that matches NO rule aborts the merge —
# nothing is guessed.
#
# Rules:
#  R1 both-added (MonolithRepository receipt assembly x3,
#     ReceiptState x2): our block adds penaltySecondsAdded, their
#     block adds forgedArtifactName at the same anchor. Both are
#     independent named args / defaulted fields — keep BOTH, comma
#     after our block's last line.
#  R2 anchor declaration (MonolithRepository): our declares nowWall
#     (needed by #113 F4's newStartWall) + old boot call; their has
#     only the nullable boot call main's merged surroundings expect
#     (Int?). Keep nowWall + their declaration.
#  R3 finish controller (MonolithViewModel): our single arm
#     `else -> onFinishE0(row, kind, witnessed = false)` retires the
#     >10-min discard per Audit R3 #113 F3; their arms keep the
#     discard for manual finishes (Audit R1 #40). R3 is the later,
#     specific judgment and subsumes #40's sleep exemption (sleep
#     stand-downs issue unwitnessed through the same arm). Take OUR.
#  R4 sentence when (TactileDeckScreen): Jammed line from THEIR
#     (main's newer jamSentenceFor(armPhase, nowMono) signature),
#     Proving line from THEIR (main #98 provingSentenceFor), running
#     line from OUR (#113 F2b refusalSentenceFor while running).

SEG = re.compile(r"<<<<<<< [^\n]*\n(.*?)\n=======\n(.*?)\n>>>>>>> [^\n]*\n", re.S)


def lines(block):
    return block.split("\n")


def rule_r1(our, their):
    if "penaltySecondsAdded" in our and "forgedArtifactName" in their:
        ol = lines(our)
        if not ol[-1].rstrip().endswith(","):
            ol[-1] = ol[-1] + ","
        return "\n".join(ol + lines(their)) + "\n"
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
    # Re-anchor block (MonolithRepository): our carries #113 F4's
    # both-anchors law (comment + newStartWall); their carries the
    # nullable boot probe's guards (null-checked rebootDetected,
    # `currentBoot ?: session.bootCount`). Resolved: our comment,
    # their reboot/accumulated/mono lines, our newStartWall, their
    # newBootCount.
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
        new_start_wall = grab(ol, "val newStartWall")
        new_boot_count = grab(tl, "val newBootCount")
        if i_reboot < 0 or i_t_mono < 0 or not new_start_wall or not new_boot_count:
            return None
        comment = ol[:i_reboot]
        theirs_head = tl[: i_t_mono + 1]
        # drop their leading comment line(s): everything before
        # their `val rebootDetected`.
        i_t_reboot = idx(tl, "val rebootDetected")
        theirs_head = tl[i_t_reboot : i_t_mono + 1]
        return "\n".join(comment + theirs_head + [new_start_wall, new_boot_count]) + "\n"
    return None


RULES = [rule_r1, rule_r2, rule_r3, rule_r4, rule_r5]

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
