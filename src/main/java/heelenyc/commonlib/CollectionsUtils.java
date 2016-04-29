package heelenyc.commonlib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @className:org.im.core.util.CollectionsUtils.java
 * @description:Collections Utils
 * @author <a href="mailto:sun.xusen@immomo.com">Andy</a>
 * @date 2014-11-21 下午5:56:21
 */
public class CollectionsUtils {
	

	public static <T> List<T> toList(Set<T> set) {
		
		List<T> list = new ArrayList<T>(); 
		for (T val : set) {
			list.add(val);
		}
		return list;
	}
	
	public static <T> Set<T> toSet(T[] ts){
		Set<T> set = new HashSet<T>();
		for (T t : ts) {
			set.add(t);
		}
		return set;
	}
	
	public static <T> List<T> toList(T[] ts){
		List<T> list = new ArrayList<T>();
		for (T t : ts) {
			list.add(t);
		}
		return list;
	}
	
}
