package jdbc.repository.exception;

public class RepositoryAccessException extends RuntimeException {

    public static final String defaultMessage = "Repository access error";

    public RepositoryAccessException(Exception e) {
        super(defaultMessage);
        initCause(e);
    }

    public RepositoryAccessException(String message, Exception e) {
        super(message);
        initCause(e);
    }
}
