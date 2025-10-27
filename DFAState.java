import java.util.HashMap;
import java.util.Map;

public class DFAState {
    private int stateId;
    private boolean isAccepting;
    private TokenType tokenType;
    private Map<Character, DFAState> transitions;
    private DFAState defaultTransition;

    public DFAState(int stateId, boolean isAccepting, TokenType tokenType) {
        this.stateId = stateId;
        this.isAccepting = isAccepting;
        this.tokenType = tokenType;
        this.transitions = new HashMap<>();
        this.defaultTransition = null;
    }

    public DFAState(int stateId) {
        this(stateId, false, null);
    }

    public void addTransition(char c, DFAState nextState) {
        transitions.put(c, nextState);
    }

    public void addRangeTransition(char start, char end, DFAState nextState) {
        for (char c = start; c <= end; c++) {
            transitions.put(c, nextState);
        }
    }

    public void setDefaultTransition(DFAState nextState) {
        this.defaultTransition = nextState;
    }

    public DFAState transition(char c) {
        if (transitions.containsKey(c)) {
            return transitions.get(c);
        }
        return defaultTransition;
    }

    public boolean isAccepting() {
        return isAccepting;
    }

    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public int getStateId() {
        return stateId;
    }
}