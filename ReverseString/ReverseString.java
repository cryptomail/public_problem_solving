
public class ReverseString {

	private String s;
	public ReverseString(String s) {
		this.s = s;
		
	}
	
	public String reverse() {
		if(s == null) {
			return null;
		}
		if(s.length() == 0 || s.length() == 1) {
			return s;
		}
		char sa[] = s.toCharArray();
		
		int begin = 0;
		int end = s.length()-1;
		
		while(end > begin) {
			char temp;
			
			temp = sa[begin];
			sa[begin] = sa[end];
			sa[end] = temp;
			end--;
			begin++;
			
		}
		
		return new String(sa);
	}
	public static void main(String args[]) {
		ReverseString reverse = new ReverseString("HI THERE this is good");
		System.out.println(reverse.reverse());
		ReverseString reverse2 = new ReverseString(null);
		System.out.println(reverse2.reverse());
		ReverseString reverse3 = new ReverseString("1");
		System.out.println(reverse3.reverse());
		ReverseString reverse4 = new ReverseString("12");
		System.out.println(reverse4.reverse());
		ReverseString reverse5 = new ReverseString("123");
		System.out.println(reverse5.reverse());
		ReverseString reverse6 = new ReverseString("1234");
		System.out.println(reverse6.reverse());
	}
}
