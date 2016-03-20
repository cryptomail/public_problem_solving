package zigZag;
import java.util.ArrayList;

class CharLoc {
	public int x;
	public Character c;
};
enum DIRECTION {
	DOWN,
	DIAG
};
/*
0123456789abc
P   A   H   N
A P L S I I G
Y   I   R
*/
public class zigZag {

	protected String emitLines(ArrayList<ArrayList<CharLoc>> lines) {
		String str="";
		
		for(ArrayList<CharLoc> line : lines) {
			for(int x = 0; x < line.size(); x++) {
				str += line.get(x).c;
				if(x + 1 < line.size()) {
					int thisx = line.get(x).x;
					int nextx = line.get(x+1).x;
					for(int z = 0; z < (nextx - thisx)-1; z++) {
						str += " ";
					}
				}
			}
			str += "\n";
		}
		return str;
	}
	public String zig(String str, int numlines) {
		if(numlines <= 0) {
			return null;
		}
		if(str == null || str.length() <= 0 || numlines <= 1 ) {
			return (str == null) ? null : str;
		}
		
		ArrayList<ArrayList<CharLoc>> lines = new ArrayList<ArrayList<CharLoc>>();
		int x;
		for(x=0; x < numlines; x++) {
			lines.add(new ArrayList<CharLoc>());
		}
		int current_x = 0;
		int current_y = 0;
		int y_multiplier = -1;
		DIRECTION d = DIRECTION.DOWN;
		for(x=0; x < str.length(); x++) {
			char c = str.charAt(x);
			
			
			switch(d) {
				case DOWN:
				{
					
					CharLoc cloc = new CharLoc();
					cloc.c = c;
					cloc.x = current_x;
					lines.get(current_y).add(cloc);
					if((current_y + 1) % numlines == 0) {
						d = DIRECTION.DIAG;
						y_multiplier = -y_multiplier;
						current_y += -1;
						current_x += numlines-1;
					}
					else {
						current_y += 1;
					}
				}
				break;
				default:
				{
					
					CharLoc cloc = new CharLoc();
					cloc.c = c;
					cloc.x = current_x;
					lines.get(current_y).add(cloc);
					if(current_y % numlines == 0) {
						d = DIRECTION.DOWN;
						current_y += 1;
						
						
					} else {
						current_x += numlines-1;
						current_y -= 1;
					}
					
				}
				break;
			}
		}
		
		return emitLines(lines);
	}
	public static void main(String args[]) {
		zigZag zig = new zigZag();
		System.out.println(zig.zig("PAYPALISHIRING",3));
		return;
	}
}
