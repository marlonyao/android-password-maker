package marlon.passwordmaker.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleTest {
	private static void md5(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytes = message.getBytes("iso-8859-1");
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bytes);
		byte[] digestBytes = md.digest();
		String result1 = Util.toString(digestBytes);
		
		MD5 md5 = new MD5();
		md5.update(bytes);
		digestBytes = md5.digest();
		String result2 = Util.toString(digestBytes);
		
		if (result1.equals(result2)) {
			System.out.println(message + ": " + result1);
		} else {
			System.err.println("======= ERROR =======");
			System.out.println("expected " + result1 + ", but get " + result2 + " for message '" + message + "'");
		}
	}
	
	private static void sha256(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytes = message.getBytes("iso-8859-1");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(bytes);
		byte[] digestBytes = md.digest();
		String result1 = Util.toString(digestBytes);
		
		Sha256 sha256 = new Sha256();
		sha256.update(bytes);
		digestBytes = sha256.digest();
		String result2 = Util.toString(digestBytes);
		
		if (result1.equals(result2)) {
			System.out.println(message + ": " + result1);
		} else {
			System.err.println("======= ERROR =======");
			System.out.println("expected " + result1 + ", but get " + result2 + " for message '" + message + "'");
		}
	}
	
	public static void main(String[] args) throws Exception {
		md5("hello world");
		md5("你好，中国");
		
		sha256("hello world");
		sha256("你好，中国");
	}
}
