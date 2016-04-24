/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
public class Solution {
    public ListNode oddEvenList(ListNode head) {
        if(head == null) {
            return null;
        }
        ListNode lastOdd = null;
        ListNode lastEven = null;
        ListNode oddHead = null;
        ListNode evenHead = null;
        ListNode current = head;
        ListNode ret = null;
        
        int x = 0;
        
        while(current != null) {
            if((x % 2) == 0) {
                if(oddHead == null) {
                    oddHead = new ListNode(current.val);
                    lastOdd = oddHead;
                }
                else {
                    lastOdd.next = new ListNode(current.val);
                    lastOdd = lastOdd.next;
                }
                
                
            }
            else {
                 if(evenHead == null) {
                    evenHead = new ListNode(current.val);
                    lastEven = evenHead;
                }
                else {
                    lastEven.next = new ListNode(current.val);
                    lastEven = lastEven.next;
                }
            }
            
            x++;
            x = (x % 2);
            current = current.next;
        }
        ret = oddHead;
        lastOdd.next = evenHead;
        
        return ret;
    }
}
