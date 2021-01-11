import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestA {
	
	/*Suppose we have a list of integers for example [2,3,4,5,6,7,8], 
	write a function that returns true if 
	there is a pair of numbers that sums up to a target sum, 
	say 11 or return false otherwise. (Done)*/
	
	@Test
	public boolean test(List<Integer> integers, int target) {
		
		int[] nums = new int[integers.size()];
		int end = integers.size() - 1;
		int start = 0;
		
		for(int i = 0; i < integers.size(); i++)
			nums[i] = integers.get(i);
		
		Arrays.sort(nums);
		
		/*while (start != end) {
			System.out.println(nums[start]);
			System.out.println(nums[end]);
			if((nums[start] + nums[end]) == target)
				return true;
			if((nums[start] + nums[end]) < target){
				start++;
				
			}else if ((nums[start] + nums[end]) > target) {
				end--;
				
			}
			
			//if()
			
		}*/
		//11
		//2,3,4,5,6,7,8
		for(int i = 0; i < nums.length; i++) {
			System.out.println(nums[start]);
			System.out.println(nums[end]);
			if((nums[start] + nums[end]) == target)
				return true;
			if((nums[start] + nums[end]) < target){
				start++;
				continue;
			}else if ((nums[start] + nums[end]) > target) {
				end--;
				continue;
			}
		}
		
		return false;
	}
	
	/*public static void main(String[] args) {
		TestA t = new TestA();
		List.of(2,3,4,5,6,7,8);
		System.out.print(t.test(List.of(2,3,4,5,6,7,8), 15));
	}*/

}
