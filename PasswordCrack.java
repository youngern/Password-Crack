/*PasswordCrack.java
The assignment must be executed by java PasswordCrack inputFile1 inputFile2 where inputFile1 is the dictionary and inputFile2 is a list of passwords to crack.
*/
import java.util.*;
import java.lang.StringBuilder;
import java.io.*;
public class PasswordCrack{
	static String[] clist = {"!","@","#","$","%","^","&", "*", "(", ")",
                             "0", "1", "2", "3", "4", "5", "6", "7","8","9"}; 
	public static void main(String[] args) throws IOException{
        
		File dictionary = new File(args[0]); //dictionary
		File passwords = new File(args[1]); //list of passwords to crack
		
        String input = "";
        String[] text = new String[6];
        String salt = "";
        String pass = "";
        String firstName = "";
        String lastName = "";
        Scanner sc = new Scanner(passwords);
        long start = System.currentTimeMillis();
        long end = 0;
        int count = 1;
        int found = 0;
        while(sc.hasNextLine()){
           //input line in the password crack text
           input = sc.nextLine();
           text = input.split(":");
           //extract the encrypted password and salt
           salt = text[1].substring(0,2); //first two letters of encrypted text
           pass = text[1];
           //augment the wordlist with words that user might have used
           firstName = (text[4].split(" "))[0];
           lastName = (text[4].split(" "))[1];
           //Systematically encrypt words and compare
           System.out.printf("Password #%d\n", count);
           System.out.println("Checking for general words....");
           boolean tf = compare(dictionary, firstName,lastName,salt,pass);
           if(tf){
        	  System.out.println("Password Found.");
              end = System.currentTimeMillis();
              System.out.println("Time elapsed: " + ((end - start) / 100 / 60));
              found++;
           }
           else{
             //redo with mangled versions of words
             System.out.println("Checking for mangled words....");
             boolean tf2 = compareWithMangle(dictionary, firstName, lastName, salt, pass);
             if(tf2){
        	    System.out.println("Password Found.");
        	    end = System.currentTimeMillis();
                System.out.println("Time elapsed: " + ((end - start) / 100 / 60));
                found++;
             }
             else{
        	    System.out.println("Checking for double-mangled words...");
                boolean tf3 = compareWithMangle2(dictionary, firstName, lastName, salt, pass);
                if(tf3){
                   System.out.println("Password Found.");
        	       end = System.currentTimeMillis();
                   System.out.println("Time elapsed: " + ((end - start) / 100 / 60));
                   found++;
                }
                else{
                   System.out.println("Password not found.");
                }
             }
            }
            count++;
        }
        System.out.printf("Total passwords: %d, Found passwords: %d\n",count,found);
        System.out.println("Total time elapsed: " + ((end - start) / 100 / 60) + " minutes");    
	}
	//compares mangled strings
	public static boolean compareWithMangle(File dictionary, String firstName, String lastName, String salt, String pass) throws FileNotFoundException{
        
        Scanner scan = new Scanner(dictionary);
        String result = "";
        int i = 0;
        while(scan.hasNextLine()){
           String nl;
           if(i == 0){nl = firstName;}
           else if(i == 1){nl = lastName;}
           else{nl = scan.nextLine();}
           String[] m = mangle1(nl);
           for(String s : m){
               result = jcrypt.crypt(salt,s);
               if(result.equals(pass)){
               	  System.out.println(s);
               	  return true;
               }
           }
           i++;
        }
        return false;

	}
	//compares double mangled strings
	public static boolean compareWithMangle2(File dictionary, String firstName, String lastName, String salt, String pass) throws FileNotFoundException{
        
        Scanner scan = new Scanner(dictionary);
        String result = "";
        int i = 0;
        while(scan.hasNextLine()){
           String nl;
           if(i == 0){nl = firstName;}
           else if(i == 1){nl = lastName;}
           else{nl = scan.nextLine();}
           String[] m = mangle2(nl);
           for(String s : m){
               result = jcrypt.crypt(salt,s);
               if(result.equals(pass)){
               	  System.out.println(s);
               	  return true;
               }
           }
           i++;
        }
        return false;

	}
	//compares regular strings
	public static boolean compare(File dictionary, String firstName, String lastName, String salt, String pass) throws FileNotFoundException{
        String result = "";
        Scanner scan = new Scanner(dictionary);
        int i = 0;
        while(scan.hasNextLine()){
           String nl;
           if(i == 0){nl = firstName;}
           else if(i == 1){nl = lastName;}
           else{nl = scan.nextLine();}
		   result = jcrypt.crypt(salt,nl);
		   if(result.equals(pass)){
		       System.out.println(nl);    
              return true;
            }
           i++;
        }
        return false;
	}
    //mangles strings twice
	public static String[] mangle1(String word){
		String[] mangled = new String[52];
        for(int i = 0; i < 20; i++){
        	mangled[i] = clist[i] + word;
        	mangled[i+20] = word + clist[i]; 
        }
		mangled[40] = word.substring(1);
		mangled[41] = word.substring(0,word.length()-1);
        mangled[42] = new StringBuilder(word).reverse().toString();
		mangled[43] = word + word;
		mangled[44] = word + new StringBuilder(word).reverse().toString();
		mangled[45] = new StringBuilder(word).reverse().toString() + word;
		mangled[46] = word.toUpperCase();
		mangled[47] = word.toLowerCase();
		mangled[48] = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
		mangled[49] = Character.toLowerCase(word.charAt(0)) + word.substring(1).toUpperCase();
		mangled[50] = toggle1(word);
		mangled[51] = toggle2(word);
		return mangled;
	}
	//mangles string twice
	public static String[] mangle2(String word){
		String[] mangle_1 = mangle1(word);
		String[] mangled = new String[2704];
		int i = 0;
		for(String m : mangle_1){
			String[] mangled2 = mangle1(m);
           for(int j = 0; j < 52; j++){
           	  mangled[i] = mangled2[j];
           	  i++;
           }
		}
        return mangled;
	}
	//returns toggled string with first character uppercase
	public static String toggle1(String word){
      		String new_word = "";
      		for(int i = 0; i < word.length(); i++){
			if(i % 2 == 0){
			    new_word += Character.toUpperCase(word.charAt(i));
			}
			else{
				new_word += Character.toLowerCase(word.charAt(i));
			}
		}
		return new_word;
	}
	//returns toggled string with first character lowercase
	public static String toggle2(String word){
		    String new_word = "";
      		for(int i = 0; i < word.length(); i++){
			if(i % 2 == 0){
			    new_word += Character.toLowerCase(word.charAt(i));
			}
			else{
				new_word += Character.toUpperCase(word.charAt(i));
			}
		}
		return new_word;
	}


}