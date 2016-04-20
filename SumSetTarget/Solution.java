import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Solution {

    public HashMap<Integer,List<List<Integer>>> solutions;
    public static void main(String[] args) {
	// write your code here

        int cand[] = {14,6,25,9,30,20,33,34,28,30,16,12,31,9,9,12,34,16,25,32,8,7,30,12,33,20,21,29,24,17,27,34,11,17,30,6,32,21,27,17,16,8,24,12,12,28,11,33,10,32,22,13,34,18,12};
        int target = 27;

        Solution solution = new Solution();
        List<List<Integer>> lists = new ArrayList<>();
       // solution.getCombos(2,candidates,0,11,lists);

        for(int z = 1; z < cand.length; z++) {
            solution.getCombos(z,cand,0,target,lists);
        }
        System.out.println(lists);
    }

    public void getCombos(int setSize, int []candidates, int index,int targetSum, List<List<Integer>> outputList) {

        /*
        setsize: 2
        0,10,1,11  --> 0 10, 0 1, 0 11, 10 1, 10 11, 1 11
         */
        if(index >= candidates.length) {
            return;
        }

        HashMap<String,List<Integer>> listHashMap = new HashMap<>();
        for(int x = 0; x < candidates.length; x++) {
            int base = index;


            List<Integer> list = new ArrayList<>();
            list.add(candidates[base]);
            for(int y = 1; y < setSize; y++) {

                int target = (base + y + x)  % candidates.length;
                if(target != base) {
                    list.add(candidates[target]);
                }

            }

            boolean bFound = false;
            if(list.size() == setSize) {
                Collections.sort(list);
                if(!listHashMap.containsKey(list.toString())) {
                    listHashMap.put(list.toString(), list);
                    for(List<Integer> list1 : outputList) {
                        if(list1.toString().equals(list.toString())) {
                            bFound = true;
                            break;
                        }
                    }

                    if(!bFound) {
                        if(list.stream().mapToInt(i -> i).sum() == targetSum) {
                            outputList.add(list);
                        }

                    }

                }
            }


        }


        getCombos(setSize,candidates,index+1,targetSum,outputList);

    }

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {

        List<List<Integer>> lists = new ArrayList<>();

        for(int z = 1; z < candidates.length; z++) {
            getCombos(z,candidates,0,target,lists);
        }
        return lists;
    }
}

