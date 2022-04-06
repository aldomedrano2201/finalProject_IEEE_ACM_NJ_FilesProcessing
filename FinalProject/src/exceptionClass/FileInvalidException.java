package exceptionClass;



public class FileInvalidException extends Exception{
	
	public FileInvalidException(String field,String fileName,int fileNumber,String fileExtension) {
		
		super("\nERROR DETECTED EMPTY FIELD\n==========================\n\nFile is invalid: Input file "+fileName+fileNumber+"."+fileExtension+
				"\nFile is invalid: It cannot be parsed due to missing field information " + field+". File will not be processed!");
		
	}

	public FileInvalidException(String structFileMessage) {
		super(structFileMessage);
	}

	
}
