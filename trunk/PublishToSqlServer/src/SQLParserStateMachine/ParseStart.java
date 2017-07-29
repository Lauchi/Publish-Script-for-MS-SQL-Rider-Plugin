package SQLParserStateMachine;

public class ParseStart extends SQLParseState {
    @Override
    public SQLParseState parse(String token) {
        if (token.toUpperCase() == "CREATE") {
            return new CreateState();
        }
        return new IllegalState();
    }
}
