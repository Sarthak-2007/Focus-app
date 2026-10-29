#!/usr/bin/env bash
# test wrapper: acquire, re-acquire (re-entrancy), hold, release
Q="$1"; LANE="$2"; HOLD="$3"
QUEUE_DIR="$4" bash "$Q" acquire "$LANE" --timeout 30 || exit 1
QUEUE_DIR="$4" bash "$Q" acquire "$LANE" --timeout 30   # re-entrant: must print "already holding"
sleep "$HOLD"
QUEUE_DIR="$4" bash "$Q" release "$LANE"
