package com.dongxiawu.uikit;

/**
 * @author wudongxia
 * @date 2020/10/27
 */
class StateSet {
    /**
     * The order here is very important to
     * {@link android.view.View#getDrawableState()}
     */
    private static final int[][] VIEW_STATE_SETS;

    public static final int VIEW_STATE_WINDOW_FOCUSED = 1;

    public static final int VIEW_STATE_SELECTED = 1 << 1;

    public static final int VIEW_STATE_FOCUSED = 1 << 2;

    public static final int VIEW_STATE_ENABLED = 1 << 3;

    public static final int VIEW_STATE_PRESSED = 1 << 4;

    public static final int VIEW_STATE_ACTIVATED = 1 << 5;

    public static final int VIEW_STATE_ACCELERATED = 1 << 6;

    public static final int VIEW_STATE_HOVERED = 1 << 7;

    public static final int VIEW_STATE_DRAG_CAN_ACCEPT = 1 << 8;

    public static final int VIEW_STATE_DRAG_HOVERED = 1 << 9;

    public static final int VALUE_STATE_FOCUSED = 0x0101009c;
    public static final int VALUE_STATE_WINDOW_FOCUSED = 0x0101009d;
    public static final int VALUE_STATE_ENABLED = 0x0101009e;
    public static final int VALUE_STATE_SELECTED = 0x010100a1;
    public static final int VALUE_STATE_PRESSED = 0x010100a7;
    public static final int VALUE_STATE_ACTIVATED = 0x010102fe;
    public static final int VALUE_STATE_ACCELERATED = 0x0101031b;
    public static final int VALUE_STATE_HOVERED = 0x01010367;
    public static final int VALUE_STATE_DRAG_CAN_ACCEPT = 0x01010368;
    public static final int VALUE_STATE_DRAG_HOVERED = 0x01010369;
    private static final int[] SORTED_VIEW_DRAWABLE_STATES = new int[] {
            VALUE_STATE_FOCUSED,
            VALUE_STATE_WINDOW_FOCUSED,
            VALUE_STATE_ENABLED,
            VALUE_STATE_SELECTED,
            VALUE_STATE_PRESSED,
            VALUE_STATE_ACTIVATED,
            VALUE_STATE_ACCELERATED,
            VALUE_STATE_HOVERED,
            VALUE_STATE_DRAG_CAN_ACCEPT,
            VALUE_STATE_DRAG_HOVERED,
    };

    private static final int[] VIEW_STATE_IDS = new int[]{
            VALUE_STATE_WINDOW_FOCUSED, VIEW_STATE_WINDOW_FOCUSED,
            VALUE_STATE_SELECTED, VIEW_STATE_SELECTED,
            VALUE_STATE_FOCUSED, VIEW_STATE_FOCUSED,
            VALUE_STATE_ENABLED, VIEW_STATE_ENABLED,
            VALUE_STATE_PRESSED, VIEW_STATE_PRESSED,
            VALUE_STATE_ACTIVATED, VIEW_STATE_ACTIVATED,
            VALUE_STATE_ACCELERATED, VIEW_STATE_ACCELERATED,
            VALUE_STATE_DRAG_HOVERED, VIEW_STATE_HOVERED,
            VALUE_STATE_DRAG_CAN_ACCEPT, VIEW_STATE_DRAG_CAN_ACCEPT,
            VALUE_STATE_HOVERED, VIEW_STATE_DRAG_HOVERED
    };

    static {
        if ((VIEW_STATE_IDS.length / 2) != SORTED_VIEW_DRAWABLE_STATES.length) {
            throw new IllegalStateException(
                    "VIEW_STATE_IDs array length does not match ViewDrawableStates style array");
        }

        final int[] orderedIds = new int[VIEW_STATE_IDS.length];
        for (int i = 0; i < SORTED_VIEW_DRAWABLE_STATES.length; i++) {
            final int viewState = SORTED_VIEW_DRAWABLE_STATES[i];
            for (int j = 0; j < VIEW_STATE_IDS.length; j += 2) {
                if (VIEW_STATE_IDS[j] == viewState) {
                    orderedIds[i * 2] = viewState;
                    orderedIds[i * 2 + 1] = VIEW_STATE_IDS[j + 1];
                }
            }
        }

        final int NUM_BITS = VIEW_STATE_IDS.length / 2;
        VIEW_STATE_SETS = new int[1 << NUM_BITS][];
        for (int i = 0; i < VIEW_STATE_SETS.length; i++) {
            final int numBits = Integer.bitCount(i);
            final int[] set = new int[numBits];
            int pos = 0;
            for (int j = 0; j < orderedIds.length; j += 2) {
                if ((i & orderedIds[j + 1]) != 0) {
                    set[pos++] = orderedIds[j];
                }
            }
            VIEW_STATE_SETS[i] = set;
        }
    }


    public static int[] get(int mask) {
        if (mask >= VIEW_STATE_SETS.length) {
            throw new IllegalArgumentException("Invalid state set mask");
        }
        return VIEW_STATE_SETS[mask];
    }


    public StateSet() {
    }

    /**
     * A state specification that will be matched by all StateSets.
     */
    public static final int[] WILD_CARD = new int[0];

    /**
     * A state set that does not contain any valid states.
     */
    public static final int[] NOTHING = new int[]{0};

    /**
     * Return whether the stateSetOrSpec is matched by all StateSets.
     *
     * @param stateSetOrSpec a state set or state spec.
     */
    public static boolean isWildCard(int[] stateSetOrSpec) {
        return stateSetOrSpec.length == 0 || stateSetOrSpec[0] == 0;
    }

    /**
     * Return whether the stateSet matches the desired stateSpec.
     *
     * @param stateSpec an array of required (if positive) or
     *                  prohibited (if negative) {@link android.view.View} states.
     * @param stateSet  an array of {@link android.view.View} states
     */
    public static boolean stateSetMatches(int[] stateSpec, int[] stateSet) {
        if (stateSet == null) {
            return (stateSpec == null || isWildCard(stateSpec));
        }
        int stateSpecSize = stateSpec.length;
        int stateSetSize = stateSet.length;
        for (int i = 0; i < stateSpecSize; i++) {
            int stateSpecState = stateSpec[i];
            if (stateSpecState == 0) {
                // We've reached the end of the cases to match against.
                return true;
            }
            final boolean mustMatch;
            if (stateSpecState > 0) {
                mustMatch = true;
            } else {
                // We use negative values to indicate must-NOT-match states.
                mustMatch = false;
                stateSpecState = -stateSpecState;
            }
            boolean found = false;
            for (int j = 0; j < stateSetSize; j++) {
                final int state = stateSet[j];
                if (state == 0) {
                    // We've reached the end of states to match.
                    if (mustMatch) {
                        // We didn't find this must-match state.
                        return false;
                    } else {
                        // Continue checking other must-not-match states.
                        break;
                    }
                }
                if (state == stateSpecState) {
                    if (mustMatch) {
                        found = true;
                        // Continue checking other other must-match states.
                        break;
                    } else {
                        // Any match of a must-not-match state returns false.
                        return false;
                    }
                }
            }
            if (mustMatch && !found) {
                // We've reached the end of states to match and we didn't
                // find a must-match state.
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether the state matches the desired stateSpec.
     *
     * @param stateSpec an array of required (if positive) or
     *                  prohibited (if negative) {@link android.view.View} states.
     * @param state     a {@link android.view.View} state
     */
    public static boolean stateSetMatches(int[] stateSpec, int state) {
        int stateSpecSize = stateSpec.length;
        for (int i = 0; i < stateSpecSize; i++) {
            int stateSpecState = stateSpec[i];
            if (stateSpecState == 0) {
                // We've reached the end of the cases to match against.
                return true;
            }
            if (stateSpecState > 0) {
                if (state != stateSpecState) {
                    return false;
                }
            } else {
                // We use negative values to indicate must-NOT-match states.
                if (state == -stateSpecState) {
                    // We matched a must-not-match case.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check whether a list of state specs has an attribute specified.
     *
     * @param stateSpecs a list of state specs we're checking.
     * @param attr       an attribute we're looking for.
     * @return {@code true} if the attribute is contained in the state specs.
     * @hide
     */
    public static boolean containsAttribute(int[][] stateSpecs, int attr) {
        if (stateSpecs != null) {
            for (int[] spec : stateSpecs) {
                if (spec == null) {
                    break;
                }
                for (int specAttr : spec) {
                    if (specAttr == attr || -specAttr == attr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int[] trimStateSet(int[] states, int newSize) {
        if (states.length == newSize) {
            return states;
        }

        int[] trimmedStates = new int[newSize];
        System.arraycopy(states, 0, trimmedStates, 0, newSize);
        return trimmedStates;
    }
}
