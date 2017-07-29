package SQLParserStateMachine;

public class CreateState extends SQLParseState {
    @Override
    public SQLParseState parse(String token) {
        if (token.toUpperCase() == "PROCEDURE") {
            return new CreateProcedureStat();
        }
        return new IllegalState();
    }
}
