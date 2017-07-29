package SQLParserStateMachine;

public class IllegalState extends SQLParseState {

    @Override
    public SQLParseState parse(String token) {
        return new IllegalState();
    }
}
