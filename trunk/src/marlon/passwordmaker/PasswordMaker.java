package marlon.passwordmaker;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import marlon.passwordmaker.hash.MD5;
import marlon.passwordmaker.hash.Sha256;

/*
 * Author: marlon yao<yaolei135@gmail.com>
 */
public class PasswordMaker {
	public static String GeneratorPassword2(String passwdMaster,
			String passwdUrl, String charset, String hashAlgorithm,
			int passwdLength, String modifier) {
		String password = "";
		int count = 0;
		while (password.length() < passwdLength) {
			// To maintain backwards compatibility with all previous versions of
			// passwordmaker, the first call to _generatepassword() must use the plain "key".
			// Subsequent calls add a number to the end of the key so each
			// iteration doesn't generate the same hash value.
			password += (count == 0) ? generatepassword(hashAlgorithm, passwdMaster, passwdUrl + modifier, passwdLength, charset)
					: generatepassword(hashAlgorithm, passwdMaster + '\n' + count, passwdUrl + modifier, passwdLength, charset);
			count++;
		}
		return password.substring(0, passwdLength);
	}

	private static String generatepassword(String hashAlgorithm, String key,
			String data, int passwordLength, String charset) {
		key += data;

		// apply the algorithm
		String password = "";
		if ("sha256".equals(hashAlgorithm)) {
			password = any_sha256(key, charset);
		} else if ("md5".equals(hashAlgorithm)) {
			password = any_md5(key, charset);
		} else {
			throw new IllegalArgumentException("unknown hash alogrithm '"
					+ hashAlgorithm + "'");
		}
		return password;
	}

	private static String any_md5(String s, String e) {
		MD5 hashAlg = new MD5();
		try {
			hashAlg.update(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			hashAlg.update(s.getBytes());
		}
		return rstr2any(hashAlg.digest(), e);
	}

	private static String any_sha256(String s, String e) {
		Sha256 hashAlg = new Sha256();
		try {
			hashAlg.update(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			hashAlg.update(s.getBytes());
		}
		return rstr2any(hashAlg.digest(), e);
	}

	private static String rstr2any(byte[] input, String encoding) {
		int divisor = encoding.length();
		List<Integer> remainders = new ArrayList<Integer>();

		// Convert to an array of 16-bit big-endian values, forming the dividend
		List<Integer> dividend = new ArrayList<Integer>();
		// pad this
		while (dividend.size() < Math.ceil(input.length / 2.0))
			dividend.add(0);
		String inp;
		try {
			inp = new String(input, "ISO-8859-1"); // Because Miquel is a lazy twit and didn't want to do a search and replace
		} catch (UnsupportedEncodingException e) {
			inp = new String(input);
		}
		for (int i = 0; i < dividend.size(); i++) {
			dividend.set(i, (inp.charAt(i * 2) << 8) | inp.charAt(i * 2 + 1));
		}

		// Repeatedly perform a long division. The binary array forms the
		// dividend, the length of the encoding is the divisor. Once computed, the
		// quotient forms the dividend for the next step. We stop when the dividend is
		// zero. All remainders are stored for later use.
		while (dividend.size() > 0) {
			List<Integer> quotient = new ArrayList<Integer>();
			int x = 0;
			for (int i = 0; i < dividend.size(); i++) {
				x = (x << 16) + dividend.get(i);
				int q = x / divisor;
				x -= q * divisor;
				if (quotient.size() > 0 || q > 0)
					quotient.add(q);
			}
			remainders.add(x);
			dividend = quotient;
		}

		// Convert the remainders to the output string
		String output = "";
		for (int i = remainders.size() - 1; i >= 0; i--) {
			output += encoding.charAt(remainders.get(i));
		}

		return output;
	}

}
