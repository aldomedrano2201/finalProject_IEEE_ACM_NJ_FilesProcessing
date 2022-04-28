/**
 * 
 * Student names: Jaina Manik, Aldo Medrano
 * Student numbers: 2111628, 2032115
 * Date: 18-04-2022
 * 
 * Description of the project:
 * The following projects executes the data process of bib files in order to get output files
 * according to the IEEE, ACM, and NJ standards. 
 * 
 * The initial process is to validate the structure of the fields for each standard which are defined in three structure files.
 * Each file defines the order the fields values to be written in the output files.
 * 
 * Such files are:
 * IEEEStruct.txt
 * ACMStruct.txt
 * NJStruct.txt
 * 
 * After the validation of the fields order (validateFiles) and their structure, the application validates the findings of empty fields 
 * It searches for empty fields on every article section.
 * 
 * If the validation is successful the application  to create the fields based on the guidelines IEEE, ACM and NJ.
 * 
 * After files creation, the program asks to the user to review any of the files created.
 * 
 */

package baseClasses;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import exceptionClass.FileInvalidException;

public class BibCreator{

	/**
	 * Declaration of the structures files  
	 */
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
			processFilesForValidation(filename,fileNumber,fileExtension);
			reviewFile();
		} catch (FileInvalidException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
			
			
		
		scanner.close();
		
	}

	/**
	 * Reads the user input file name to review its content
	 */
	private static void reviewFile() {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		int tries=1;
		
		do {
			try
	        {
				
	            System.out.print("\n\nPlease enter the name of file you need to review: ");
	            String nameOfFile = sc.nextLine();
	            BufferedReader reader1 = new BufferedReader(new FileReader(nameOfFile+".json"));
	            String str = reader1.readLine();
	            while(str!=null)
	            {
	                System.out.println(str);
	                str = reader1.readLine();
	                tries=3;
	            }
	        }catch(FileNotFoundException e)
	        {
	            System.out.println("Could not open input file. File doesn't exists; possibly it could not be created!");
	            if (tries==1)
	            	System.out.println("However, you will be allowed another chance to enter another file name");
	            else {
	            	System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
	            	sc.close();
	            	System.exit(0);
	            } 
					
				
	            
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tries++;
		} while (tries<=2);
		
		System.out.println("Goodbye! Hope you enjoyed creating the needed files using Bibcreator");
		sc.close();
		
	}

	/**
	 * 
	 * @param filename
	 * @param fileNumber
	 * @param fileExtension
	 * @throws FileInvalidException
	 * 
	 * Executes the validation of the files against a definition of each file structure (IEE, ACM , NJ) defined in different files
	 */
	private static void processFilesForValidation(String filename, int fileNumber, String fileExtension) throws FileInvalidException {
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

	/**
	 * 
	 * @param filename
	 * @param fileNumber
	 * @param fileExtension
	 * 
	 * Read the files to check if they have empty fields and to create them if they are valid
	 */
	private static void readFiles(String filename, int fileNumber,String fileExtension)  {
		// TODO Auto-generated method stub
		Scanner readFile = null;
		LinkedList<String> articles= new LinkedList<String>();
		LinkedList<LinkedList<String>> listArticles=new LinkedList<LinkedList<String>>();
		String lineProcessed="";
		int numErrorFiles=0; 
		int numSuccesFiles=0;
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
							numSuccesFiles++;
						} else {
							
							numErrorFiles++;
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
				
				
					
		}
		//Check if there were invalid files to display the message
		if (numErrorFiles>0) 
			System.out.println("\nA total of "+numErrorFiles+" were invalid, and could not be processed. All other "+numSuccesFiles+" were created.");

	}

	/**
	 * 
	 * @param listArticles
	 * @param fileNumber
	 * @throws FileNotFoundException
	 * @throws FileInvalidException
	 * 
	 * Create the files according to the json structure
	 */
	private static void createFiles(LinkedList<LinkedList<String>> listArticles,int fileNumber) throws FileNotFoundException, FileInvalidException{
		//Process files IEEE,ACM and NJ based on the file content read from the file structure
				processFileEEE(listArticles,fileNumber);
				processFileACM(listArticles,fileNumber);
				processFileNJ(listArticles,fileNumber);
		
	}

	/**
	 * 
	 * @param listArticles
	 * @param fileNumber
	 * @throws FileNotFoundException
	 * @throws FileInvalidException
	 * 
	 * Create the ACM file
	 */
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

	/**
	 * 
	 * @param listArticles
	 * @param fileNumber
	 * @throws FileInvalidException
	 * @throws FileNotFoundException
	 * 
	 * Create the NJ file
	 */
	private static void processFileNJ(LinkedList<LinkedList<String>> listArticles, int fileNumber) throws FileInvalidException, FileNotFoundException {
		
		PrintWriter fileWriter =  new PrintWriter(new FileOutputStream("NJ"+fileNumber+".json"));
		Path path = Paths.get("NJ"+fileNumber+".json");
		LinkedList<String> st=getStructure(STRUCT_FILE_NJ);
		if (st==null) {
			fileWriter.close();
			throw new FileInvalidException("Could not process NJ struct file, program will terminate!");
			
		}
			
		
		String NJString="";
		
		for (LinkedList<String> article : listArticles) {
			
			String year="";
			
			for (String ACMfield : st) {
				
				for (String field : article) {
					
					
					if (field.contains(ACMfield)) {
						
						
						switch (field.substring(0, field.indexOf("="))) {
						case "author":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							if (field.contains("and"))
								NJString=NJString.replace("and", "&");
							else
								NJString=NJString+". ";
							break;
						case "title":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							break;
						case "journal":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2)+". ";
							break;
						case "volume":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2)+", ";
							break;
						case "number":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2);
							break;
						case "pages":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-2)+"("+year+").";
							break;
						case "month":
							NJString=NJString+field.substring(field.indexOf("=")+1,field.length()-1)+" ";
							break;
						case "year":
							year=field.substring(field.indexOf("=")+1,field.length()-2);
							break;
						case "doi":
							NJString=NJString+"DOI:https://doi.org/"+field.substring(field.indexOf("=")+1,field.length()-2)+".";
							break;
						default:
							break;
						}
						
					}
				
				}
			
			}
			
			year="";
			NJString=NJString+"\n\n";
		}
		 // file exists and it is not a directory
		  if(validateFile(path)) {
		      fileWriter.append(NJString);
		  }else {
			  fileWriter.write(NJString);
		  }
		  fileWriter.close();
	}

	/**
	 * 
	 * @param listArticles
	 * @param fileNumber
	 * @throws FileNotFoundException
	 * @throws FileInvalidException
	 * 
	 * Create the IEEE file
	 */
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

	/**
	 * 
	 * @param path
	 * @return
	 * 
	 * Validate if the structure file and directory exist (NJ, IEEE, ACM)
	 */
	private static boolean validateFile(Path path) {
		// TODO Auto-generated method stub
		if(Files.exists(path) && !Files.isDirectory(path))
			return true;
		return false;
	}

	/**
	 * 
	 * @param fileStructure
	 * @return
	 * 
	 * Reads the structure of the files (ACM,NJ,IEEE) to get the fields to create the output files
	 */
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

	/**
	 * 
	 * @param readFile
	 * @return
	 * 
	 * Validates empty fields in the input files
	 */
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


