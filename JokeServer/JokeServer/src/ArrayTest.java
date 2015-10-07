import java.util.Scanner;

/**
 * 
 */

/**
 * @author ppadm
 *
 */
public class ArrayTest {

	/**
	 * @param args
	 */
	static String[] sampple={"a","b","c","d","e"};
	static int ct=0;
	public static void print() {
		
		System.out.println(sampple[ct]);
		ct++;
		if (ct>sampple.length-1) 
			ct=0;
		System.out.println("hi");

	}

	public static void main(String[] args) {
		Scanner sc =new Scanner(System.in);
	for (int i=1; i<=5;i++){
		System.out.println("hit enter to continue");
		String a=sc.nextLine();
		if(a.equals(""))
			ArrayTest.print();
		}
	}
}
