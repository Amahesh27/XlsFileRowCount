package ExceptionHandling;

public class InvalidArgumentsException extends RuntimeException{

	public InvalidArgumentsException(String message) {
		super(message);
	}
}
