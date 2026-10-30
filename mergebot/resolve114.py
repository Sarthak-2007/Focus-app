import sys

# STURM3-11 mergebot resolver for agent/issue-114 — REPORT-ONLY stub.
# Round 1 learns the conflict surface against current main; the bot
# captures unmerged files + segments into mergebot/status-114.txt
# before this runs. Always exits 1 so the merge aborts untouched
# until STURM3-11 writes real rules from the reported segments.
print("report-only stub: no resolution attempted")
sys.exit(1)
