/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.link.monitor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

/**
 *
 * @author Vikram
 */
public class TpLink
{

    static HttpURLConnection getConnecttion(String call, String qr) throws MalformedURLException, IOException
    {
        URL u = new URL("http://192.168.0.1/userRpm/" + call + ".htm?" + qr);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestProperty("Referer", "http://192.168.0.1/userRpm/SystemStatisticRpm.htm");
        con.setRequestProperty("Authorization", "Basic VmlrcmFtZDU6dmlrcmFtZA==");
        return con;
    }

    static Matcher getMatcher(String call, String qr, String pattern) throws IOException
    {
        HttpURLConnection con = getConnecttion(call, qr);
        Scanner sc = new Scanner(con.getInputStream());
        sc.useDelimiter("\\)\\;");
        String s = sc.next();
        // System.out.println(s);
        con.disconnect();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        return m;
    }

    static int toKB(String s)
    {
        return Integer.parseInt(s) / 1024;
    }

    static String toSize(long n)
    {
        if (n < 1024)
        {
            return String.format("%d Bytes", n);
        } else if (n < 1024000)
        {
            return String.format("%.2f KB", n / 1024.0);
        } else if (n < 1024000000)
        {
            return String.format("%.2f MB", n / 1024000.0);
        } else
        {
            return String.format("%.2f GB", n / 1024000000.0);
        }
    }

    static String toSize(String s)
    {
        long n = Long.parseLong(s);
        return toSize(n);
    }

    static SyslogIF getLogger()
    {
        SyslogIF l = Syslog.getInstance("udp");
        l.getConfig().setHost("localhost");
        l.getConfig().setPort(514);
        l.getConfig().setLocalName("TP-Link Monitor");
        return l;
    }
}
