package heelenyc.commonlib;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class StringUtils extends org.apache.commons.lang.StringUtils {

    public static String escape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c))
                tmp.append(c);
            else if (c < 256) {
                tmp.append("\\");
                if (c < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(c, 16));
            } else {
                tmp.append("\\u");
                tmp.append(Integer.toString(c, 16));
            }
        }
        return tmp.toString();
    }

    public static String upperCaseFirstChar(String str) {
        return str.toUpperCase().substring(0, 1) + str.substring(1);
    }
    
    /**
     * convert &quot; to HTML Entities"
     * @param html
     */
    public static String unescapeHtml(String html) {
        return StringEscapeUtils.unescapeHtml("");
    }

    /**
     * replace '{i}' in the message with parameters, 'i' is the index of the param
     * eg. ("I am {0} years old", 21), then '21' will replace the '{i}' in the string
     * so the result is "I am 21 years old"
     * @param message
     * @param params
     */
    public static String format(String message, Object... params) {
        if (message == null) return null;
        if (params == null || params.length == 0) return message;
        
        for (int i = 0; i < params.length; i++) {
            // Matcher.quoteReplacement will fix the bug of replaceAll: No Group 5. quote "$" to "\$"
            message = message.replaceAll("\\{"+ i +"\\}", params[i] == null ? "null" : Matcher.quoteReplacement(String.valueOf(params[i].toString())));
        }
        
        return message;
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(StringUtils.escape("wankeA"));
        
        Pattern p = Pattern.compile("^\\s*(\\w*)\\s*=\\s*"); 
        Matcher m = p.matcher("forex={\"city\":\"Beijing\",1:2,2:{\"city\":4}}"); 
        while (m.find()) { 
            String s0 = m.group(1); 
            System.out.println(s0); 
            System.out.println(m.replaceFirst(""));
        } 
        
        System.out.println(StringUtils.format("I am {0}, age: {1}", "Leon", 213.1));
    }

}
