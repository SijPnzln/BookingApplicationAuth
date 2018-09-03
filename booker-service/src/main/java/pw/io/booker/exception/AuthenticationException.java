package pw.io.booker.exception;

public class AuthenticationException extends Exception{
	private String errorMessage;

	public AuthenticationException(Exception e, String errorMessage) {
		super(e);
		this.errorMessage = errorMessage;
	}
	
	public AuthenticationException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String getMessage() {
		return errorMessage;
	}
	
	
}
