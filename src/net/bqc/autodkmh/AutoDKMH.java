package net.bqc.autodkmh;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * Tool for automatically registering courses of VNU Created by cuong on
 * 12/2/2015. Updated on 20/12/2016
 */
public class AutoDKMH {

    public final static String HOST = "http://dangkyhoc.vnu.edu.vn";

    public final static String LOGIN_URL = HOST + "/dang-nhap";
    public final static String LOGOUT_URL = HOST + "/Account/Logout";
    public final static String AVAILABLE_COURSES_DATA_URL = HOST + "/danh-sach-mon-hoc/1/1"; // only
                                                                                             // available
                                                                                             // course
                                                                                             // for
                                                                                             // faculty
    public final static String AVAILABLE_COURSES_DATA_URL_2 = HOST + "/danh-sach-mon-hoc/1/2"; // all
                                                                                               // courses
    public final static String REGISTERED_COURSES_DATA_URL = HOST + "/danh-sach-mon-hoc-da-dang-ky/1";
    public final static String CHECK_PREREQUISITE_COURSES_URL = HOST + "/kiem-tra-tien-quyet/%s/1"; // %s
                                                                                                    // for
                                                                                                    // data-crdid
    public final static String CHOOSE_COURSE_URL = HOST + "/chon-mon-hoc/%s/1/1"; // %s
                                                                                  // for
                                                                                  // data-rowindex
    public final static String SUBMIT_URL = HOST + "/xac-nhan-dang-ky/1";

    public final static String USER_AGENT = "Mozilla/5.0";
    public final static String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public final static String ACCEPT_LANGUAGE = "en-US,en;q=0.5";

    private HttpURLConnection con;

    // user's information
    private String user;
    private String password;
    private ArrayList<String> courseCodes;

    private List<Course> courses;

    // sleep time
    private long sleepTime;

    public AutoDKMH() {
        courseCodes = new ArrayList<>();
        courses = new ArrayList<>();
        loadInitialParameters("config.properties");
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        AutoDKMH tool = new AutoDKMH();

        // tool.sendGet(HOST);

        System.out.println("/******************************************/");
        System.out.println("//! Username = " + tool.user.substring(0, 6) + "**");
        // not support for password under 2 characters :P
        System.out.println("//! Password = " + "********");
        System.out.println("//! Course Codes = " + tool.courseCodes);
        System.out.println("/******************************************/");

        tool.run();

    }

    private void printCookies(CookieManager cookieManager) {
        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {

            // gets domain set for the cookie
            System.out.println("Domain: " + cookie.getDomain());

            // gets max age of the cookie
            System.out.println("max age: " + cookie.getMaxAge());

            // gets name cookie
            System.out.println("name of cookie: " + cookie.getName());

            // gets path of the server
            System.out.println("server path: " + cookie.getPath());

            // gets boolean if cookie is being sent with secure protocol
            System.out.println("is cookie secure: " + cookie.getSecure());

            // gets the value of the cookie
            System.out.println("value of cookie: " + cookie.getValue());

            // gets the version of the protocol with which the given cookie is
            // related.
            System.out.println("value of cookie: " + cookie.getVersion());
        }

    }

    private void run() throws IOException, InterruptedException {
        // turn on cookie
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        Calendar cal = Calendar.getInstance();

        while (true) {
            System.out.println("\n/******************************************/");
            System.out.println("Try on: " + cal.getTime().toString());

            try {
                doLogin();
            } catch (Exception e) {
                System.err.println("\nEncounter with exception " + e.getMessage());
                System.out.println("Try again...");
                continue;
            }

            /*
             * get list of courses and the course details by given course code
             */
            System.out.print("Get raw courses data...");

            // get available courses of faculty, need do it before submitting
            // new course
            String coursesData = sendPost(AVAILABLE_COURSES_DATA_URL, "");
            // must get this shit before submitting a new course >.<
            sendPost(REGISTERED_COURSES_DATA_URL, "");
            // get all available courses of school
            coursesData = sendPost(AVAILABLE_COURSES_DATA_URL_2, "");

            System.out.println("[Done]");

            for (int i = 0; i < courseCodes.size(); i++) {
                System.out.print("\nGetting course information for [" + courseCodes.get(i) + "]...");
                String courseDetails[] = getCourseDetailsFromCoursesData(coursesData, courseCodes.get(i));
                System.out.println("[Done]");

                /* register courses and submit them */
                if (courseDetails != null) {
                    // check prerequisite courses
                    System.out.print("Checking prerequisite courses...");
                    String res = sendPost(String.format(CHECK_PREREQUISITE_COURSES_URL, courseDetails[0]), "");
                    System.out.println("[Done]");
                    System.out.println("Response: " + res);
                    // choose course
                    System.out.print("Choose [" + courseCodes.get(i) + "] for queue...");
                    res = sendPost(String.format(CHOOSE_COURSE_URL, courseDetails[1]), "");
                    System.out.println("[Done]");
                    System.out.println("Response: " + res);
                    // submit registered courses
                    System.out.print("Submitting...");
                    res = sendPost(String.format(SUBMIT_URL, ""), "");
                    System.out.println("[Done]");
                    System.out.println("Response: " + res);
                    // remove after being registered
                    if (res.contains("thành công"))
                        courseCodes.remove(i);
                }
            }

            // logout
            System.out.print("Logging out...");
            sendGet(LOGOUT_URL);
            System.out.println("[Success]");

            if (courseCodes.isEmpty()) {
                System.out.println("\nRegistered all!\n[Exit]");
                System.exit(1);
            }

            System.out.println("/******************************************/");
            Thread.sleep(sleepTime);
        }
    }

    private void doLogin() throws IOException {
        /*
         * load login site to get cookie and login parameters then login using
         * post
         */
        System.out.print("Getting cookies, token...");
        String loginSiteHtml = sendGet(LOGIN_URL);
        System.out.println("[Done]");

        System.out.print("Logging in...");
        String loginParams = getFormParams(loginSiteHtml, user, password);
        String res = sendPost(LOGIN_URL, loginParams);
        if (!res.contains("<title>Trang ch\u1EE7")) {
            System.out.println("[Fail]");
            System.exit(1);
        }
        System.out.println("[Success]");
    }

    private void loadInitialParameters(String filePath) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream(filePath);
            Properties p = new Properties();
            p.load(is);

            this.user = p.getProperty("usr");
            this.password = p.getProperty("passwd");

            String rawCourseCodes = p.getProperty("course_codes");
            String[] courseCodesArr = rawCourseCodes.split("\\.");
            courseCodes.addAll(Arrays.asList(courseCodesArr));

            String sleepTimeStr = p.getProperty("sleep_time");
            this.sleepTime = Long.parseLong(sleepTimeStr);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * get data-crdid and data-rowindex of a course
     * 
     * @param coursesDataHtml
     * @param courseCode
     *            the given code of course
     * @return the first element is data-crdid and the second is data-rowindex
     *         if the course is available. Otherwise, return null
     */
    private String[] getCourseDetailsFromCoursesData(String coursesDataHtml, String courseCode) {
        coursesDataHtml = "<table id=\"coursesData\">" + coursesDataHtml + "</table>";
        Document doc = Jsoup.parse(coursesDataHtml);
        Elements elements = doc.select("#coursesData").select("tr");

        /* find course on courses list which owns the given course code */
        for (Element e : elements) {
            if (e.toString().contains(courseCode)) {
                /*
                 * data-cridid and data-rowindex always are at the first input
                 * tag if the course is available
                 */
                Element inputElement = e.getElementsByTag("input").get(0);

                if (inputElement.hasAttr("data-rowindex")) { // the course is
                                                             // available for
                                                             // registering
                    String crdid = inputElement.attr("data-crdid");
                    String rowindex = inputElement.attr("data-rowindex");
                    return new String[] { crdid, rowindex };
                }
            }
        }

        return null; // the course is not available
    }

    /**
     * get parameters for login action
     * 
     * @param html
     *            parse to get cookie and parameters from this
     * @param user
     *            user value parameter
     * @param passwd
     *            password value parameter
     * @return all parameters in a string
     */
    private String getFormParams(String html, String user, String passwd) {
        Document doc = Jsoup.parse(html);
        List<String> params = new ArrayList<>();

        // login form
        Elements elements = doc.getAllElements();
        Element loginForm = elements.first();
        Elements inputElements = loginForm.getElementsByTag("input");

        // generate parameters
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("LoginName")) {
                value = user;
            } else if (key.equals("Password")) {
                value = passwd;
            }

            params.add(key + "=" + value);
        }

        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            if (builder.length() == 0) {
                builder.append(param);

            } else
                builder.append("&").append(param);
        }

        return builder.toString();
    }

    /**
     * send post method
     * 
     * @param urlStr
     *            url for post
     * @param postParams
     *            parameters
     * @return response from server
     * @throws IOException
     */
    private String sendPost(String urlStr, String postParams) throws IOException {
        URL url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setUseCaches(false);
        con.setDoOutput(true);
        // con.setConnectTimeout(1000000);

        // set properties
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", ACCEPT);
        con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        // check result code
        // int responseCode = con.getResponseCode();
        // System.out.println("\nSending 'POST' request to URL : " + url);
        // System.out.println("Post parameters : " + postParams);
        // System.out.println("Response Code : " + responseCode);

        // get content
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    /**
     * send get method
     * 
     * @param urlStr
     *            url for get
     * @return response from server
     * @throws IOException
     */
    private String sendGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setUseCaches(false);
        // con.setConnectTimeout(1000000);

        // set properties
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", ACCEPT);
        con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

        // check result code
        // int responseCode = con.getResponseCode();
        // System.out.println("\nSending 'GET' request to URL : " + url);
        // System.out.println("Response Code : " + responseCode);

        // get result content
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}