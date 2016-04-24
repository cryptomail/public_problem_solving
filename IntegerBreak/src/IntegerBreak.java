import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


/*
 * Given a positive integer n, break it into the sum of at least two positive integers
 * and maximize the product of those integers. Return the maximum product you can get.
 * For example, given n = 2, return 1 (2 = 1 + 1); given n = 10, return 36 (10 = 3 + 3 + 4).
 */
public class IntegerBreak {

	public static void main(String args[]) {
		System.out.println("hi");
		
		IntegerBreak i = new IntegerBreak();
		System.out.println(i.integerBreak(15));
	}
	
	private HashMap<Integer,Integer> results = new HashMap<>();
	
	
	public int integerBreak(int n) {
		
		
		if(n == 0) {
			return 0;
		}
		if(n == 1) {
			return 1;
		}
		if(n == 2) {
			return 1;
		}
		if(results.containsKey(n)) {
			return results.get(n);
		}
		boolean bStop = false;
		int divisor = 2;
		int maxpsum = 0;
		int psum = 0;
		
		while(divisor  < n) {
			
			
			int times = n / divisor;
			int leftover = n % divisor;
			if(leftover > 0) {
				int p1 = (int)(Math.pow(divisor,times));
				int p2 = Math.max(integerBreak(leftover),leftover);

				int psum1 = p1*p2;
				int psum2 = 0;
				if(times > 1) {
					int p11 = (int)(Math.pow(divisor,times-1));
					int p22 = Math.max(integerBreak(leftover+divisor),divisor+leftover);
					
					psum2 = p11*p22;
				}
				
				
				psum = Math.max(psum1,psum2);
				
			}
			else {
				psum = (int)(Math.pow(times,divisor));
			}
			if(psum >= maxpsum) {
				maxpsum = psum;
			} 
			divisor++;
			
		}
		
		results.put(n, maxpsum);
		return maxpsum;
	}
}
