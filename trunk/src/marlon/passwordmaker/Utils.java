package marlon.passwordmaker;

import java.util.List;

/*
 * Author: marlon yao<yaolei135@gmail.com>
 */
public class Utils {
	public static String join(List<String> cols, String sep) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < cols.size(); i++) {
			if (i != 0)
				sb.append(sep);
			sb.append(cols.get(i));
		}
		return sb.toString();
	}
}
