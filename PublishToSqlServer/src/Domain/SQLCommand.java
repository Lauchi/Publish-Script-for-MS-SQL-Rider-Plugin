package Domain;

public abstract class SQLCommand {
    private String entityName;
    private String body;

    protected SQLCommand(String entityName, String body) {
        this.entityName = entityName;
        this.body = body;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getBody() {
        return body;
    }
}
