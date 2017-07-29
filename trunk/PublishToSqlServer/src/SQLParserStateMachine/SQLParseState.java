package SQLParserStateMachine;

public abstract class SQLParseState {
    public abstract SQLParseState parse(String token);
}
