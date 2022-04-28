package exceptionClass;

/**
 * 
 * @author Aldo and Jaina
 *
 * Class that catches the throwing exception of business logic in files' validations 
 */

public class FileInvalidException extends Exception{
	
	/**
	 * 
	 * @param field
	 * @param fileName
	 * @param fileNumber
	 * @param fileExtension
	 * 
	 * Fields validation exceptions
	 */
	public FileInvalidException(String field,String fileName,int fileNumber,String fileExtension) {
		
		super("\nERROR DETECTED EMPTY FIELD\n==========================\n\nFile is invalid: Input file "+fileName+fileNumber+"."+fileExtension+
				"\nFile is invalid: It cannot be parsed due to missing field information " + field+". File will not be processed!");
		
	}

	/**
	 * 
	 * @param structFileMessage
	 
	 *	File structure validation exceptions
	 */
	public FileInvalidException(String structFileMessage) {
		super(structFileMessage);
	}

	
}
