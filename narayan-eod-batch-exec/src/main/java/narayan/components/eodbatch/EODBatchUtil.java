package narayan.components.eodbatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringJoiner;

/**
 *
 * @author narayana
 *
 */
class EODBatchUtil {

    private static final DateFormat YYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String nowInISO(){
        return YYYYMMDDHHMMSS.format(Calendar.getInstance().getTime());
    }

    public static String getFileName(String prefix, String nowInISO) {
        return prefix + nowInISO + ".txt";
    }

    public static String getFileAbsolutePath(String path, String name) {
        return path + "/" + name;
    }

    public static String prepareHeader(String delimiter, String headerMarker, String fileName, String nowInISO) {
        return new StringJoiner(delimiter).add(headerMarker).add(fileName).add(nowInISO).toString();
    }

    public static String prepareFooter(String delimiter, String footerMarker, Integer writeCount) {
        return footerMarker + delimiter + writeCount;
    }
}
