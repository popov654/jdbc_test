package jdbc.repository.exception;

public class RepositoryAccessException extends Throwable {

    public static final String defaultMessage = "Repository access error";

    public RepositoryAccessException(Throwable e) {
        super(defaultMessage);
        initCause(e);
    }

    public RepositoryAccessException(String message, Throwable e) {
        super(message);
        initCause(e);
    }
}
