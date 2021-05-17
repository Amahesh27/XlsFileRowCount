package ExceptionHandling;

public class RowNumberNotExistException extends RuntimeException{

	public RowNumberNotExistException(String message) {
		super(message);
	}
	
}
