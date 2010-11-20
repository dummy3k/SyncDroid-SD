package de.syncdroid;

public class Utils {
	public static String combinePath(String path, String path2) {
        if(path.endsWith("/")) {
            return path + path2;
        } else {
            return path + '/' + path2;
        }
	}
}
