import java.util.Arrays;
import java.util.HashMap;

public class Anagram {

	public boolean isAnagram1(String s1, String s2) {
		
		if(s1 == null || s2 == null) {
			return false;
		}
		s1 = s1.replace(" ", "").toLowerCase();
		s2 = s2.replace(" ", "").toLowerCase();
		
		if(s1.length() != s2.length()) {
			return false;
		}
		
		char a1[] = s1.toCharArray();
		char a2[] = s2.toCharArray();
		Arrays.sort(a1);
		Arrays.sort(a2);
		
		if(Arrays.equals(a1, a2)) {
			return true;
		}
		
		return false;
	}
	public boolean isAnagram2(String s1, String s2) {
		
		if(s1 == null || s2 == null) {
			return false;
		}
		s1 = s1.replace(" ", "").toLowerCase();
		s2 = s2.replace(" ", "").toLowerCase();
		
		if(s1.length() != s2.length()) {
			return false;
		}
		HashMap<Character,Integer> h1 = new HashMap<Character,Integer>();
		HashMap<Character,Integer> h2 = new HashMap<Character,Integer>();
		char a1[] = s1.toCharArray();
		char a2[] = s2.toCharArray();
		
		for(Character c : a1) {
			if(!h1.containsKey(c)) {
				h1.put(c, 1);
			}
			else {
				h1.put(c, h1.get(c)+1);
			}
		}
		for(Character c : a2) {
			if(!h2.containsKey(c)) {
				h2.put(c, 1);
			}
			else {
				h2.put(c, h2.get(c)+1);
			}
		}
		
		for(Character c : h1.keySet()) {
			if((!h2.containsKey(c)) || (h2.get(c) != h1.get(c)) ) {
				return false;
			}
		}
		for(Character c : h2.keySet()) {
			if((!h1.containsKey(c)) || (h2.get(c) != h1.get(c)) ) {
				return false;
			}
		}
		
		
		return true;
	}
	public static void main(String args[]) {
		
		Anagram program = new Anagram();
		
		System.out.println(program.isAnagram1("Mother In Law", "Hitler Woman"));
		System.out.println(program.isAnagram1("keEp", "peeK"));
		System.out.println(program.isAnagram1("SiLeNt CAT", "LisTen AcT"));
		System.out.println(program.isAnagram1("Debit Card", "Bad Credit"));
		System.out.println(program.isAnagram1("School MASTER", "The ClassROOM"));
		System.out.println(program.isAnagram1("DORMITORY", "Dirty Room"));
		System.out.println(program.isAnagram1("ASTRONOMERS", "NO MORE STARS"));
		System.out.println(program.isAnagram1("Toss", "Shot"));
		System.out.println(program.isAnagram1("joy", "enjoy"));
		
		System.out.println("************BLOCK 2******************");
		System.out.println(program.isAnagram2("Mother In Law", "Hitler Woman"));
		System.out.println(program.isAnagram2("keEp", "peeK"));
		System.out.println(program.isAnagram2("SiLeNt CAT", "LisTen AcT"));
		System.out.println(program.isAnagram2("Debit Card", "Bad Credit"));
		System.out.println(program.isAnagram2("School MASTER", "The ClassROOM"));
		System.out.println(program.isAnagram2("DORMITORY", "Dirty Room"));
		System.out.println(program.isAnagram2("ASTRONOMERS", "NO MORE STARS"));
		System.out.println(program.isAnagram2("Toss", "Shot"));
		System.out.println(program.isAnagram2("joy", "enjoy"));
	}
}
