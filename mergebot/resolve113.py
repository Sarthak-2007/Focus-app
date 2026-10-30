import re
import subprocess
import sys

# STURM3-11 mergebot resolver for agent/issue-113.
# The ONLY known conflict between agent/issue-113 and main is the
# sentence `when` in TactileDeckScreen.kt:
#   our  (issue #113 F2b): Proving -> null;
#        isSessionRunning -> refusalSentenceFor(deckWarrant, nowMono)
#   their (main, issue #98): Proving -> provingSentenceFor(armPhase);
#        isSessionRunning -> null
# Resolution keeps BOTH cures: Proving speaks main's proving sentence;
# a running session speaks #113's refusal sentence. Any other conflict
# segment or file aborts the merge (exit 1) — nothing is guessed.

files = subprocess.check_output(
    ["git", "diff", "--name-only", "--diff-filter=U"], text=True
).split()
if not files:
    print("no unmerged files; nothing to resolve")
    sys.exit(1)

for path in files:
    if not path.endswith("TactileDeckScreen.kt"):
        print("unknown conflicted file:", path)
        sys.exit(1)
    src = open(path).read()
    pat = re.compile(
        r"<<<<<<< [^\n]*\n(.*?)\n=======\n(.*?)\n>>>>>>> [^\n]*\n", re.S
    )

    def repl(m):
        our, their = m.group(1), m.group(2)
        if (
            "ArmPhase.Proving -> null" in our
            and "provingSentenceFor(armPhase)" in their
            and "isSessionRunning -> refusalSentenceFor(deckWarrant, nowMono)" in our
            and re.search(r"isSessionRunning -> null", their)
        ):
            indent_m = re.search(r"(\s*)armPhase is ArmPhase\.Proving", our)
            indent = indent_m.group(1) if indent_m else "                    "
            return (
                f"{indent}armPhase is ArmPhase.Proving -> provingSentenceFor(armPhase)\n"
                f"{indent}isSessionRunning -> refusalSentenceFor(deckWarrant, nowMono)\n"
            )
        raise ValueError("unknown conflict segment in " + path)

    try:
        out = pat.sub(repl, src)
    except ValueError as e:
        print(e)
        sys.exit(1)
    if "<<<<<<<" in out or ">>>>>>>" in out:
        print("conflict markers remain in", path)
        sys.exit(1)
    if out == src:
        print("no segment matched in", path)
        sys.exit(1)
    open(path, "w").write(out)

print("resolved:", files)
