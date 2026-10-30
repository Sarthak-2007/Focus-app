import re
import subprocess
import sys

# STURM3-11 mergebot resolver for agent/issue-114 vs current main.
# Every rule is pattern-matched to a conflict segment STURM3-11 read
# and decided by hand (mergebot/status-114.txt). Anything else
# aborts — nothing is guessed.
#
#  P1 (MonolithViewModel, start2MinutePeek): our side is EMPTY at
#     the anchor; their side is main's richer launch block (issue
#     #99 F4 hand-merge note + #75 F1 failure logging via MLog).
#     The branch's F1 intent — launch the peeked package so the
#     lease stops burning on hand navigation — is fully contained
#     in main's version, which adds the failure log. Take THEIR.
#     (Post-merge check: exactly one getLaunchIntentForPackage
#     launch site in the merged function.)
#  P2 (AirlockAndDisarmOverlays): both sides replaced the same
#     KDoc+function region — ours with #114 F3's truth texts
#     (peekCooldownScopeText / peekOfferPreviewText), theirs with
#     main's #324 substitute-pack function. Both additions stand:
#     emit ours, reopen a KDoc, then theirs (theirs carries its
#     own closing */ and function; asserted before composing).

SEG = re.compile(r"<<<<<<< [^\n]*\n(.*?)\n=======\n(.*?)\n>>>>>>> [^\n]*\n", re.S)


def rule_p1(our, their):
    if our.strip() == "" and "getLaunchIntentForPackage" in their:
        return their + "\n"
    return None


def rule_p2(our, their):
    if "peekCooldownScopeText" in our and "Issue #324" in their:
        if " */" not in their or "fun " not in their:
            return None
        return our + "\n/**\n" + their + "\n"
    return None


RULES = [rule_p1, rule_p2]

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
