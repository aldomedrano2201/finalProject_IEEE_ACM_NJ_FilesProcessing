package baseClasses;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import exceptionClass.FileInvalidException;

public class BibCreator{

	static final String STRUCT_FILE_IEEE="IEEEStruct.txt";
	static final String STRUCT_FILE_ACM="ACMStruct.txt";
	static final String STRUCT_FILE_NJ="NJStruct.txt";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Welcome to BIBCreator!");
		
		Scanner scanner=new Scanner(System.in);
		System.out.print("Enter the prefix name of the file(s), the number of files and extension name: ");
		String filename=scanner.next();
		int fileNumber=scanner.nextInt();
		String fileExtension=scanner.next();
		
		try {
			processFiles(filename,fileNumber,fileExtension);
		} catch (FileInvalidException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
			
			
		
		scanner.close();
		
	}

	private static void processFiles(String filename, int fileNumber, String fileExtension) throws FileInvalidException {
		// TODO Auto-generated method stub
		Path pathIEEE = Paths.get(STRUCT_FILE_IEEE);
		Path pathACM = Paths.get(STRUCT_FILE_ACM);
		Path pathNJ = Paths.get(STRUCT_FILE_NJ);
		if (validateFile(pathIEEE) && validateFile(pathACM) && validateFile(pathNJ))
			readFiles(filename,fileNumber,fileExtension);
		else {
			throw new FileInvalidException("Invalid struct file");
		}
	}

	private static void readFiles(String filename, int fileNumber,String fileExtension)  {
		// TODO Auto-generated method stub
		Scanner readFile = null;
		LinkedList<String> articles= new LinkedList<String>();
		LinkedList<LinkedList<String>> listArticles=new LinkedList<LinkedList<String>>();
		String lineProcessed="";
		for (int i = 0; i < fileNumber; i++) {
			
				try {
					readFile = new Scanner(new FileInputStream(filename+(i+1)+"."+fileExtension));
					lineProcessed=validateEmptyFields(readFile);
					
											
						if (lineProcessed.equals("")) {
							readFile.close();
							
								readFile = new Scanner(new FileInputStream(filename+(i+1)+"."+fileExtension));
								
							while (readFile.hasNext()) {
								
								String str = readFile.nextLine();
								
								if (str.contains("@ARTICLE{")){
									
									continue;														
									
								}else if(!str.equals("}")){
									if (str.indexOf("=")!=-1) {
										
											articles.add(str.replace("{", "").replace("}", ""));
										
									
									}else {
										continue;
									}
							
								}
								else if(str.equals("}")){
									// Creating another linked list and copying
							       	listArticles.add(articles);
							       	articles=new LinkedList<String>();
									//articles.clear();
								}
									
								else if (str.equals("")) {
									continue;
								}
								
								else {
									
									throw new FileInvalidException(str,filename,(i+1),fileExtension);
								}
								
								
								
							}
							createFiles(listArticles,(i+1));
							listArticles=new LinkedList<LinkedList<String>>();
							lineProcessed="";
							
						} else {
							
							throw new FileInvalidException(lineProcessed,filename,(i+1),fileExtension);
						}
				} catch (FileNotFoundException e) {
					
					
					if (readFile != null) 
						readFile.close();
					System.out.println("Could not open "+ filename+(i+1)+"."+fileExtension +" file.");
				}
			
				catch (FileInvalidException e) {
					
					if (readFile != null)
						readFile.close();
						System.out.println(e.getMessage());
				}
					
					
					
				
				
						
			
		
			//readFile.close();
		}

	}

	

	private static void createFiles(LinkedList<LinkedList<String>> listArticles,int fileNumber) throws FileNotFoundException, FileInvalidException{
		//Process files IEEE,ACM and NJ based on the file content read from the file
				processFileEEE(listArticles,fileNumber);
				processFileACM(listArticles,fileNumber);
				processFileNJ(listArticles,fileNumber);
		
	}

	private static void processFileACM(LinkedList<LinkedList<String>> listArticles, int fileNumber) throws FileNotFoundException, FileInvalidException{
		
		PrintWriter fileWriter =  new PrintWriter(new FileOutputStream("ACM"+fileNumber+".json"));
		Path path = Paths.get("ACM"+fileNumber+".json");
		LinkedList<String> st=getStructure(STRUCT_FILE_ACM);
		if (st==null) {
			fileWriter.close();
			throw new FileInvalidException("Could not process ACM struct file, program will terminate!");
			
		}
			
		
		String ACMString="";
		int articleNumber=1;
		for (LinkedList<String> article : listArticles) {
			
			String year="";
			ACMString=ACMString+"["+articleNumber+"] ";
			for (String ACMfield : st) {
				
				for (String field : article) {
					
					
					if (field.contains(ACMfield)) {
						
						
						switch (field.substring(0, field.indexOf("="))) {
						case "author":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2);
							if (field.contains("and"))
								ACMString=ACMString.substring(0,ACMString.indexOf("and"))+"et al. ";
							else
								ACMString=ACMString+". ";
							break;
						case "title":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							break;
						case "journal":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							break;
						case "volume":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+", ";
							break;
						case "number":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+" ("+year+"), ";
							break;
						case "pages":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							break;
						case "month":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-1)+" ";
							break;
						case "year":
							ACMString=ACMString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							year=field.substring(field.indexOf("=")+1,field.length()-2);
							break;
						case "doi":
							ACMString=ACMString+"DOI:https://doi.org/"+field.substring(field.indexOf("=")+1,field.length()-2)+".";
							break;
						default:
							break;
						}
						
					}
					
					
				}
				
				
				
			
			}
			articleNumber++;	
			year="";
			ACMString=ACMString+"\n\n";
		}
		 // file exists and it is not a directory
		  if(validateFile(path)) {
		      fileWriter.append(ACMString);
		  }else {
			  fileWriter.write(ACMString);
		  }
		  fileWriter.close();
		
		
	}

	private static void processFileNJ(LinkedList<LinkedList<String>> listArticles, int fileNumber) {
		
		
	}

	private static void processFileEEE(LinkedList<LinkedList<String>> listArticles, int fileNumber) throws FileNotFoundException, FileInvalidException {
		
		PrintWriter fileWriter =  new PrintWriter(new FileOutputStream("IEEE"+fileNumber+".json"));
		Path path = Paths.get("IEEE"+fileNumber+".json");
		LinkedList<String> st=getStructure(STRUCT_FILE_IEEE);
		if (st==null) {
			fileWriter.close();
			throw new FileInvalidException("Could not process IEEE struct file, program will terminate!");
			
		}
			
		
		String iEEEString="";
		for (LinkedList<String> article : listArticles) {			
			for (String iEEEfield : st) {
				for (String field : article) {
					if (field.contains(iEEEfield)) {
						switch (field.substring(0, field.indexOf("="))) {
						case "author":
							iEEEString=iEEEString+(field.substring(field.indexOf("=")+1,field.length()-2)+".").replace(" and", ",")+" ";
							break;
						case "title":
							iEEEString=iEEEString+"\""+field.substring(field.indexOf("=")+1,field.length()-2)+"\", ";
							break;
						case "journal":
							iEEEString=iEEEString+field.substring(field.indexOf("=")+1,field.length());
							break;
						case "volume":
							iEEEString=iEEEString+"vol. "+field.substring(field.indexOf("=")+1,field.length());
							break;
						case "number":
							iEEEString=iEEEString+"no. "+field.substring(field.indexOf("=")+1,field.length());
							break;
						case "pages":
							iEEEString=iEEEString+"p. "+field.substring(field.indexOf("=")+1,field.length());
							break;
						case "month":
							iEEEString=iEEEString+field.substring(field.indexOf("=")+1,field.length()-1)+" ";
							break;
						case "year":
							iEEEString=iEEEString+field.substring(field.indexOf("=")+1,field.length()-2)+".";
							break;
						default:
							break;
						}
						
					}
					
					
				}
				
				
				
				
			}
			
			iEEEString=iEEEString+"\n\n";
		}
		 // file exists and it is not a directory
		  if(validateFile(path)) {
		      fileWriter.append(iEEEString);
		  }else {
			  fileWriter.write(iEEEString);
		  }
		  fileWriter.close();
		
	}

	private static boolean validateFile(Path path) {
		// TODO Auto-generated method stub
		if(Files.exists(path) && !Files.isDirectory(path))
			return true;
		return false;
	}

	private static LinkedList<String> getStructure(String fileStructure) {
		// TODO Auto-generated method stub
		Scanner fileStruct;
		try {
			fileStruct = new Scanner(new FileInputStream(fileStructure));
			LinkedList<String> itemsStruct =new LinkedList<String>();
			while (fileStruct.hasNext()) {
						
						StringTokenizer str = new StringTokenizer(fileStruct.next(), ",");
						while (str.hasMoreTokens()) {
							
						itemsStruct.add(str.nextToken());
							
						}
						
						
						
			}			
			fileStruct.close();
			return itemsStruct;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return null;
		
	}

	private static String validateEmptyFields(Scanner readFile) {
		// TODO Auto-generated method stub
		while (readFile.hasNext()) {
			String line = (String) readFile.next();
			if (line.indexOf("{}")!=-1)
				return line;
		}
		return "";
	}

	

	
		
}


