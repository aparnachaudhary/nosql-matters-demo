/**
 * 
 */
package net.arunoday.demo.service;

/**
 * Thrown when document is not available in the store.
 * 
 * @author Aparna Chaudhary
 * 
 */
public class DocumentNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1416081177876780584L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            exception message
	 */
	public DocumentNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            exception message
	 * @param e
	 *            exception
	 */
	public DocumentNotFoundException(String message, Throwable e) {
		super(message, e);
	}

}
