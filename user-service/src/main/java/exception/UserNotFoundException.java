package exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Юзер айди" + id + " не найден");
    }
}
