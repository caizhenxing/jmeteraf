package com.qsa.jaf;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import java.util.Calendar;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${"UMAMAHESH.G"} on 19/5/14.
 */
public class UtilityFunctions {


    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    protected static int throughtputCol;
    protected static int avgCol;
    protected static int totalRows;
    protected static int sampleCol;
    private static final String REQUEST_FAILURE_PATTERN = "(?!.*s=\"true\".*)<httpSample.*?>";
    private static int Count;

    protected static Properties properties;
    public static Logger logger = Logger.getLogger(UtilityFunctions.class.getName());

    protected static String getframeworkdir() {
        return System.getProperty("user.dir");
    }

    protected static String getcodedir() {
        return System.getProperty("user.dir")+getframeworkdir() + "//code";
    }

    protected static String gettestplanname() {
        return properties.getProperty("testplanname");
    }

    protected static String getreleasenum() {
        return properties.getProperty("releasenum");
    }

    protected static String getbuildnum() {
        return properties.getProperty("buildNum");
    }

    protected static String getsenderEmail() {
        return properties.getProperty("senderEmail");
    }

    protected static String getsenderEmailPwd() {
        return properties.getProperty("senderEmailPwd");
    }

    protected static String gettoEmail() {
        return properties.getProperty("toEmail");
    }

    protected static String getemailBody() {
        return properties.getProperty("emailBody");
    }

    protected static String getemailSignature() {
        return properties.getProperty("emailSignature");
    }

    protected static String getemailSubject() {
        return properties.getProperty("emailSignature");
    }

    protected static String getremoteaddress() {
        return properties.getProperty("remoteIPAddress");
    }

    protected static String getrunmode() {
        return properties.getProperty("runmode");
    }

    protected static String getjmeterdir() {
        return System.getProperty("user.dir")+properties.getProperty("jmeterdir");
    }

    protected static String getOS() {
        return properties.getProperty("OS");
    }

    protected static String getproductname() {
        return properties.getProperty("productname");
    }

    protected static String getjmeterbindir() {
        return String.format("%s//bin", getjmeterdir());
    }

    protected static String getjmeterlibdir() {
        return getjmeterdir() + "//lib";
    }

    protected static String getjmeterextdir() {
        return getjmeterlibdir() + "//ext";
    }

    protected static String getserveragent() {
        return getjmeterdir() + "//ServerAgent";
    }

    protected static String getresultsdir() {
        return System.getProperty("user.dir")+ properties.getProperty("resultsdir");
    }

    protected static String getresultsmasterdir() {
        return System.getProperty("user.dir")+properties.getProperty("resultsmasterdir");
    }

    protected static String getjmxdir() {
        return System.getProperty("user.dir")+properties.getProperty("jmxdir");
    }

    protected static String getjmeterdetailsummarydir() {
        return getresultsdir() + "//JmeterDetailSummary";
    }

    protected static String getjmetererrorsummarydir() {
        return getresultsdir() + "//Errors";
    }

    protected static String getjmeterSystemMetrics() {
        return getresultsdir() + "//SystemMetrics";
    }

    protected static String geterrorsdir() {
        return getresultsdir() + "//Errors";
    }

    protected static String gettestdetailsfilename(String directoryName, String fileName) {
        return directoryName + "//" + fileName + ".csv";
    }

    protected static String[] dostrsplit(String path, String splittype) {
        return StringUtils.split(path, splittype);
    }

    protected static String getstrjoin(String[] strsplit, String splittype) {
        return StringUtils.join(strsplit, splittype);
    }

    protected static String getccEmail() {
        return properties.getProperty("cc");
    }

    protected static boolean skipTests() {
        return Boolean.parseBoolean(properties.getProperty("skipTests"));
    }

    protected static boolean skipReports() {
        return Boolean.parseBoolean(properties.getProperty("skipReports"));
    }

    protected static boolean skipCPUReports() {
        return Boolean.parseBoolean(properties.getProperty("skipCPUReports"));
    }

    protected static boolean skipAggregateReports() {
        return Boolean.parseBoolean(properties.getProperty("skipAggregateReports"));
    }

    protected static boolean skipsendmail() {
        return Boolean.parseBoolean(properties.getProperty("skipsendmail"));
    }

    protected static File createDirectory(String testPlanName, String buildNum, String resultspath) {

        File dir = null;
        try {

            Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
            Date currentTime = localCalendar.getTime();
            int currentDay = localCalendar.get(Calendar.DATE);
            int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
            int currentHour = localCalendar.get(Calendar.HOUR);
            int currentMinute = localCalendar.get(Calendar.MINUTE);

            String directoryName;
            directoryName = testPlanName + "_B" + buildNum + "_" + currentDay + "D_" + currentMonth + "M_" + currentHour + "H_" + currentMinute + "M";

            dir = new File(resultspath + "//" + directoryName);
            boolean status = dir.mkdir();


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return dir;

    }

    public ArrayList<File> getDirectories(String directoryName) {
        File directory = new File(directoryName);
        ArrayList<File> dirs = new ArrayList<File>();


        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                dirs.add(file);
            }
        }

        return dirs;
    }

    private String getTime(String testPlanName, String buildNum) {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentTime = localCalendar.getTime();
        int currentDay = localCalendar.get(Calendar.DATE);
        int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
        int currentHour = localCalendar.get(Calendar.HOUR);
        int currentMinute = localCalendar.get(Calendar.MINUTE);


        return testPlanName + "_B" + buildNum + "_" + currentDay + "D_" + currentMonth + "M_" + currentHour + "H_" + currentMinute + "M";
    }

    protected static void runbatchProcess(String command, String description)  {
        Process p = null;
        try {
            if (getOS().equals("Windows")) {
                p = Runtime.getRuntime().exec("cmd /c start " + command + "//startAgent.bat");
            } else {
                p = Runtime.getRuntime().exec("nohup " + command + "//startAgent.sh &");
            }
            logger.info(description + " : The Process has been started successfully");
            p.waitFor();

            if (p.exitValue() == 0) {
                logger.info(description + " The Process has been completed successfully");
            }

        } catch (InterruptedException e) {
            logger.info(" ");
            logger.info("System Exit Detected!  Stopping Test..." + e.getMessage());
            logger.info(" ");

        } catch (IOException e) {
            logger.error("Not able to run the process" + e.getMessage());

        }


    }

    protected static void runProcess(String command, String description) throws MojoExecutionException {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            logger.info(description + " : The Process has been started successfully");

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                logger.debug(line);
            }

            int jMeterExitCode = p.waitFor();
            if (jMeterExitCode != 0) {
                throw new RuntimeException(" Not able to run the process ");
            }

        } catch (InterruptedException e) {
            logger.info(" ");
            logger.info("System Exit Detected!  Stopping Test..." + e.getMessage());
            logger.info(" ");

        } catch (IOException e) {
            logger.error("Not able to run the process" + e.getMessage());

        }


    }

    protected static void runJmeterProcess(String batName, String jmxfile, String command, String logFile, String resultcommand, String resultFileName)  {
        Process process = null;
        try {
            if (getOS().equals("Windows")) {
                process = new ProcessBuilder(batName + "//jmeter.bat", "-n", "-t", jmxfile, resultcommand, resultFileName).start();
            } else {
                process = new ProcessBuilder(batName + "//jmeter.sh", "-n", "-t", jmxfile, resultcommand, resultFileName).start();
            }
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            int jMeterExitCode = process.waitFor();
            if (jMeterExitCode != 0) {
                throw new MojoExecutionException("Not able to run the process");
            }

            logger.info("Completed Test: " + new File(jmxfile).getName());
        }

        catch (FileNotFoundException ex)
        {
            logger.error("log file will be shown in the console" + ex.getMessage());

        }
            catch (InterruptedException e) {
            logger.info(" ");
            logger.info("System Exit Detected!  Stopping Test..." + e.getMessage());
            logger.info(" ");

        } catch (IOException e) {
            logger.error("Not able to run the process" + e.getMessage());

        } catch (MojoExecutionException e) {
            logger.error("Not able to run the process" + e.getMessage());
        }



    }

    protected static void runJmeterRemoteProcess(String batName, String jmxfile, String command, String logFile, String remotehosts,String resultcommand, String resultFileName)  {
        Process process = null;
        try {
            if (getOS().equals("Windows")) {
                process = new ProcessBuilder(batName + "//jmeter.bat", "-n", "-t", jmxfile, command, logFile," -R " +remotehosts, resultcommand + resultFileName).start();
            } else {
                process = new ProcessBuilder(batName + "//jmeter.sh", "-n", "-t", jmxfile, command, logFile, " -R " +remotehosts,  resultcommand + resultFileName).start();
            }
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            int jMeterExitCode = process.waitFor();
            if (jMeterExitCode != 0) {
                throw new MojoExecutionException("Not able to run the process");
            }

            logger.info("Completed Test: " + new File(jmxfile).getName());

        } catch (InterruptedException e) {
            logger.info(" ");
            logger.info("System Exit Detected!  Stopping Test..." + e.getMessage());
            logger.info(" ");

        } catch (IOException e) {
            logger.error("Not able to run the process" + e.getMessage());

        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }


    }

    protected static void sendEmail(final String senderEmail, final String senderPassword, String subject, String msg[], String attachments[], String recipients) throws RuntimeException {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");
        MimeMessage message;

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        session.setDebug(false);

        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));

            // Set the subject, message body, and attachment.
            message.setSubject(subject);
            BodyPart textPart = new MimeBodyPart();
            textPart.setContent(msg[0], "text/plain");
            // HTML version
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(msg[1]+"<br><br>"+msg[2], "text/html");
            // Create the Multipart. Add BodyParts to it.
            Multipart mp = new MimeMultipart("mixed");
            mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            message.setContent(mp);
            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {

        }
    }

    private static int noOfRows(String filecsvPath) {
        int lineCountvalue = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filecsvPath)));
            String lineValue = "";

            while ((lineValue = br.readLine()) != null) {
                String[] values = lineValue.split(",");
                lineCountvalue++;
            }
        } catch (Exception e) {
            logger.error("Not able to get the no of rows " + e.getMessage());
        }

        return lineCountvalue;

    }

   protected static void generateHtmlReport(String csvFileNamePath, String performancehtmlPath, Properties prop) {

        int lineCountvalue = 0;
        DecimalFormat df = new DecimalFormat("0.##");

        String line = "";
        noOfRows(csvFileNamePath);
        try {

            BufferedReader br = new BufferedReader(new FileReader(new File(csvFileNamePath)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(performancehtmlPath)));

            bw.write("<table border=" + 1 + ">\n");

            bw.write("<tr>");
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                count++;
                if (count == 1) {
                    for (int i = 0; i < values.length; i++) {
                        bw.write("<th  bgcolor='#6495ED' style='font-family: Cambria; font-size: medium; font-weight: bolder; font-style: normal; font-variant: normal; text-transform: none; color: #FFFFFF'>" + prop.get(values[i]) + "</th>");
                    }
                } else {
                    for (int i = 0; i < values.length; i++) {
                        if (i == 0) {
                            bw.write("<th style='font-family: Cambria; font-size: medium; font-weight: bolder; font-style: normal; font-variant: normal; text-transform: none; color: #000000'>" + values[i] + "</th>");
                        } else if (count == lineCountvalue) {
                            double rounded = (double) Math.round(Double.parseDouble((values[i])) * 100) / 100;

                            bw.write("<td style='font-family: Cambria; font-size: medium; font-weight: bolder; font-style: normal; font-variant: normal; text-transform: none; color: #000000'>" + df.format(rounded) + "</td>");
                        } else {
                            double rounded = (double) Math.round(Double.parseDouble((values[i])) * 100) / 100;
                            bw.write("<td style='font-family: Cambria; font-size: medium; font-weight: lighter; font-style: normal; font-variant: normal; text-transform: none; color: #000000'>" + df.format(rounded) + "</td>");
                        }
                    }
                }
                bw.write("</tr>");
            }
            bw.flush();
            bw.close();
            br.close();

        } catch (Exception e) {
            logger.error("Not able to generate html report " + e.getMessage());
        }
    }

    protected static void generatecpuReports(String resultsDirPath, String jmeterextPath, String outputdir) {


        logger.info("Generating System Performance Monitoring CPU Reports ");

        try {
            if ((new File(resultsDirPath)).isDirectory()) {
                File dir = new File(resultsDirPath);
                File[] jtlfiles = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jtl");
                    }
                });


                for (File fileName : jtlfiles) {

                    String command = "java -jar " + jmeterextPath + "//CMDRunner.jar --tool Reporter --generate-" + "png" + " " + outputdir + "//" + FilenameUtils.removeExtension(fileName.getName()) + "_" + "PerfMon" + "." + "png" + " --input-jtl " + resultsDirPath + "//" + fileName.getName() + " --plugin-type " + "PerfMon";
                    logger.debug(command);
                    runProcess(command, "PerfMon");
                }


            }
        } catch (Exception ex) {
            logger.error("Not able to generate reports due to the following exception : " + ex.getMessage());
        }
    }

    protected static void generateAggregateReports(String resultsDirPath, String jmeterextPath, String fileType, String outputdir, String reportType) {

        try {
            if ((new File(resultsDirPath)).isDirectory()) {
                File dir = new File(resultsDirPath);
                File[] jtlfiles = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jtl");
                    }
                });


                for (File fileName : jtlfiles) {
                    String command = "java -jar " + jmeterextPath + "//CMDRunner.jar --tool Reporter --generate-" + fileType + " " + outputdir + "//" + FilenameUtils.removeExtension(fileName.getName()) + "." + fileType + " --input-jtl " + resultsDirPath + "//" + fileName.getName() + " --plugin-type " + reportType;
                    logger.debug(command);
                    runProcess(command, reportType);
                }


            }
        } catch (Exception ex) {
            logger.error("Not able to generate Aggregate reports due to the following exception" + ex.getMessage());
        }
    }




    protected static void generatecsvraggreports(String jmetercsvpath, String jmeterdetailPath, String jmeterextpath, String resultdirpath, Properties prop) {


        logger.info("Generating Aggregate Success Summary Report");
        generateAggregateReports(jmetercsvpath, jmeterextpath, "csv", resultdirpath, "AggregateReport");
        logger.info("Generating Aggregate Detail Report");
        generateAggregateReports(jmeterdetailPath, jmeterextpath, "csv", resultdirpath, "AggregateReport");


    }

    public static void generatepngreports(String jmeterdetailPath, String jmeterextpath, String resultdirpath) {

        logger.info("Generating HitsPerSecond Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "HitsPerSecond");
        logger.info("Generating ResponseTimesOverTime Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "ResponseTimesOverTime");
        logger.info("Generating TransactionsPerSecond Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "TransactionsPerSecond");
        logger.info("Generating BytesThroughputOverTime Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "BytesThroughputOverTime");
        logger.info("Generating LatenciesOverTime Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "LatenciesOverTime");
        logger.info("Generating ThroughputVsThreads Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "ThroughputVsThreads");
        logger.info("Generating ThreadsStateOverTime Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "ThreadsStateOverTime");
        logger.info("Generating ResponseCodesPerSecond Report");
        generateReports(jmeterdetailPath, jmeterextpath, "png", resultdirpath, "ResponseCodesPerSecond");

    }

    protected static void generateReports(String resultsDirPath, String jmeterextPath, String fileType, String outputdir, String reportType) {

        try {
            if ((new File(resultsDirPath)).isDirectory()) {
                File dir = new File(resultsDirPath);
                File[] jtlfiles = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jtl");
                    }
                });


                for (File fileName : jtlfiles) {

                    String command = "java -jar " + jmeterextPath + "//CMDRunner.jar --tool Reporter --generate-" + fileType + " " + outputdir + "//" + FilenameUtils.removeExtension(fileName.getName()) + "_" + reportType + "." + fileType + " --input-jtl " + resultsDirPath + "//" + fileName.getName() + " --plugin-type " + reportType;
                    logger.debug(command);
                    runProcess(command, reportType);
                }


            }
        } catch (Exception ex) {
            logger.error("Not able to generate reports due to the following exception: " + ex.getMessage());
        }
    }


    protected static Workbook createSpreadSheet(String csvfilePath, String csvallfilePath, String xlsfilePath, String imagePath, String testcsvPath, String jtlPath, Properties properties) throws IOException
             {



        logger.info("Generating Performance Spreadsheet Summary  Report");
        Workbook workBook = null;

        try {

            workBook = new HSSFWorkbook();
            CreationHelper helper = workBook.getCreationHelper();
            Sheet testSummarySheet = workBook.createSheet("TestSummary");
            Sheet sheet = workBook.createSheet("Jmeter_Success_Summary");

            CSVReader reader = new CSVReader(new FileReader(csvfilePath));
            String[] line;
            int rowNum = 0;
            int maxColumns = 0;

            while ((line = reader.readNext()) != null) {

                Row row = sheet.createRow((short) rowNum++);
                totalRows = rowNum - 1;
                CellStyle cellStyle = workBook.createCellStyle();
                Font timesBoldFont = workBook.createFont();
                timesBoldFont.setFontName("Cambria");
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                if (rowNum == 1) {
                    row.setHeightInPoints(40);
                    cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);

                } else if (rowNum == 2) {

                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                }
                if (maxColumns < line.length) {

                    maxColumns = line.length;
                }
                for (int index = 0; index < line.length; index++)
                {

                    if (rowNum != 1)
                    {
                        int intValue = 0;

                        if ((index > 0 & index <= 5)) {
                            Double doubleString = Double.parseDouble(line[index]);
                            intValue = doubleString.intValue();
                            line[index] = intValue + "";
                        } else if (index == 7) {
                            Double doubleString = Double.parseDouble(line[index]);
                            line[index] = doubleString * 100 + "";
                        }
                        else if(index >= 8 & index <= 10)
                        {
                            double rounded = (double) Math.round(Double.parseDouble((line[index])) * 100) / 100;
                            line[index] = rounded + "";
                        }

                    }

                    Cell createCell = row.createCell(index);
                    RichTextString createRichTextString = null;
                    createRichTextString = helper.createRichTextString(line[index]);

                    if ("average".equalsIgnoreCase(createRichTextString.toString())) {
                        avgCol = index;
                    } else if ("aggregate_report_rate"
                            .equalsIgnoreCase(createRichTextString.toString())) {
                        throughtputCol = index;
                    } else if ("aggregate_report_count"
                            .equalsIgnoreCase(createRichTextString.toString())) {

                        sampleCol = index;
                    }
                    String property = properties.getProperty(createRichTextString.toString());
                    if (property == null) {
                        property = createRichTextString.toString();
                    }
                    createCell.setCellValue(property);
                    if (rowNum == 1) {

                        cellStyle = workBook.createCellStyle();
                        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                        timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        cellStyle.setFont(timesBoldFont);
                        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                    } else if (rowNum != 1) {

                        cellStyle
                                .setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                    }
                    cellStyle.setWrapText(true);

                    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                    createCell.setCellStyle(cellStyle);
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        workBook.getSheetAt(1).autoSizeColumn(colNum);
                    }
                }

            }

            createPerformanceSummary(testSummarySheet, testcsvPath, maxColumns + 1, workBook, helper);

            Sheet imageSheet = workBook.createSheet("SystemStats");
            Drawing image_drawing = imageSheet.createDrawingPatriarch();
            int col1 = 1;
            int row1 = 1;
            int col2 = 13;
            int row2 = 33;

            File file = new File(imagePath);
            if (file.isFile()) {
            } else if (file.isDirectory()) {
                File[] listFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".png");
                    }
                });
                for (File fileItem : listFiles) {
                    Row row = imageSheet.createRow((short) row1 - 1);
                    CellStyle cellStyle = workBook.createCellStyle();
                    Font timesBoldFont = workBook.createFont();
                    timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    timesBoldFont.setFontName("Cambria");
                    cellStyle.setFont(timesBoldFont);
                    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    Cell createCell = row.createCell(1);
                    createCell.setCellValue(helper.createRichTextString(fileItem
                            .getName()));

                    ClientAnchor anchor_image = helper.createClientAnchor();
                    anchor_image.setCol1(col1);
                    anchor_image.setRow1(row1);
                    anchor_image.setAnchorType(ClientAnchor.MOVE_AND_RESIZE);
                    anchor_image.setCol2(col2);
                    anchor_image.setRow2(row2);
                    FileInputStream inputStream = new FileInputStream(fileItem);
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    int pictureIdx = workBook.addPicture(bytes,
                            Workbook.PICTURE_TYPE_PNG);
                    inputStream.close();
                    image_drawing.createPicture(anchor_image, pictureIdx);
                    row1 = row2 + 2;
                    row2 = row2 + 35;
                }
            }

            Sheet sheet1 = workBook.createSheet("Jmeter_Detail_Summary");

            reader = new CSVReader(new FileReader(csvallfilePath));
            line = null;
            rowNum = 0;
            maxColumns = 0;

            while ((line = reader.readNext()) != null) {

                Row row = sheet1.createRow((short) rowNum++);
                totalRows = rowNum - 1;
                CellStyle cellStyle = workBook.createCellStyle();
                Font timesBoldFont = workBook.createFont();
                timesBoldFont.setFontName("Cambria");
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                if (rowNum == 1) {

                    row.setHeightInPoints(40);
                    cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);

                } else if (rowNum == 2) {

                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                }
                if (maxColumns < line.length) {

                    maxColumns = line.length;
                }
                for (int index = 0; index < line.length; index++)
                {

                    if (rowNum != 1) {
                        int intValue = 0;

                        if ((index > 0 & index <= 5)) {
                            Double doubleString = Double.parseDouble(line[index]);
                            intValue = doubleString.intValue();
                            line[index] = intValue + "";
                        } else if (index == 7) {
                            Double doubleString = Double.parseDouble(line[index]);
                            line[index] = doubleString * 100 + "";
                            double rounded = (double) Math.round(Double.parseDouble((line[index])) * 100) / 100;
                            line[index] = rounded + "";
                        }
                        else if(index>=8&index<=10)
                        {
                            double rounded = (double) Math.round(Double.parseDouble((line[index])) * 100) / 100;
                            line[index] = rounded + "";
                        }

                    }

                    Cell createCell = row.createCell(index);
                    RichTextString createRichTextString = null;

                    createRichTextString = helper.createRichTextString(line[index]);

                    if ("average".equalsIgnoreCase(createRichTextString.toString())) {
                        avgCol = index;
                    } else if ("aggregate_report_rate"
                            .equalsIgnoreCase(createRichTextString.toString())) {
                        throughtputCol = index;
                    } else if ("aggregate_report_count"
                            .equalsIgnoreCase(createRichTextString.toString())) {

                        sampleCol = index;
                    }
                    String property = properties.getProperty(createRichTextString.toString());
                    if (property == null) {
                        property = createRichTextString.toString();
                    }
                    createCell.setCellValue(property);
                    if (rowNum == 1) {

                        cellStyle = workBook.createCellStyle();
                        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                        timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        cellStyle.setFont(timesBoldFont);
                        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                    } else if (rowNum != 1) {

                        cellStyle
                                .setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                    }
                    cellStyle.setWrapText(true);

                    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                    createCell.setCellStyle(cellStyle);
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        workBook.getSheetAt(3).autoSizeColumn(colNum);
                    }
                }

            }

            hasTestFailed(new File(jtlPath), workBook);
            FileOutputStream fileOut = new FileOutputStream(xlsfilePath);
            workBook.write(fileOut);
            fileOut.close();

        } catch (Exception ex) {
            logger.error("Not able to generate excel sheet report due to " + ex.getMessage());
        }

        return workBook;
    }

    protected static void createPerformanceSummary(Sheet sheet, String filePath, int colNo, Workbook workbook, CreationHelper helper) throws IOException {
        Row row = null;
        CSVReader reader = new CSVReader(new FileReader(filePath));
        String[] line;
        int rowNo = 0;
        colNo = 0;
        int y = colNo;

        try {
            logger.info(" ");
            //sheet = workbook.createSheet("TestSummary");
            while ((line = reader.readNext()) != null) {

                row = sheet.createRow(rowNo);

                colNo = y;
                CellStyle cellStyle = workbook.createCellStyle();
                Font timesBoldFont = workbook.createFont();

                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellStyle.setFillPattern(CellStyle.BIG_SPOTS);
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                if (rowNo == 0) {
                    timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);

                } else if (rowNo == 1) {
                    timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    cellStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);

                }
                else if ( rowNo == 9) {
                    timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                }
                else if (rowNo >= 11) {

                    cellStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);
                } else if (rowNo >= 2 && rowNo < 9) {
                    cellStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);
                }
                cellStyle.setWrapText(true);
                if (row != null) {
                    for (int i = 0; i < line.length; i++, colNo++) {

                        Cell createCell = null;
                        Cell createInnerCell = null;
                        createCell = row.createCell(colNo);
                        createCell.setCellValue(helper
                                .createRichTextString(line[i]));
                        if (line[i].equalsIgnoreCase("Throughput(Request/Second)")) {
                            colNo++;
                            i++;
                            createInnerCell = row.createCell(colNo);
                            createInnerCell.setCellValue(helper
                                    .createRichTextString(workbook
                                            .getSheet("Jmeter_Success_Summary")
                                            .getRow(totalRows)
                                            .getCell(throughtputCol).toString()));

                        } else if (line[i]
                                .equalsIgnoreCase("AverageResponseTime(In MilliSeconds)")) {
                            colNo++;
                            i++;
                            createInnerCell = row.createCell(colNo);
                            createInnerCell.setCellValue(helper
                                    .createRichTextString(workbook
                                            .getSheet("Jmeter_Success_Summary")
                                            .getRow(totalRows).getCell(avgCol)
                                            .toString()));
                        } else if (line[i]
                                .equalsIgnoreCase("# of Requests Processed")) {
                            colNo++;
                            i++;
                            createInnerCell = row.createCell(colNo);
                            createInnerCell.setCellValue(helper
                                    .createRichTextString(workbook
                                            .getSheet("Jmeter_Success_Summary")
                                            .getRow(totalRows).getCell(sampleCol)
                                            .toString()));

                        }
                        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                        timesBoldFont.setFontName("Cambria");
                        cellStyle.setFont(timesBoldFont);
                        createCell.setCellStyle(cellStyle);
                        if (createInnerCell != null) {
                            createInnerCell.setCellStyle(cellStyle);
                        }
                        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                            sheet.autoSizeColumn(colNum);
                        }
                    }
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        sheet.autoSizeColumn(colNum);
                    }
                }
                rowNo++;
            }

            logger.info("Excel sheet summary has been created");
        } catch (Exception ex) {
            logger.error("Not able to generate excel summary sheet report" + ex.getMessage());
        }

    }

    protected static int hasTestFailed(File file, Workbook workBook) throws IOException {

        Count = 0;
        Scanner resultFileScanner;
        Pattern errorPattern = Pattern.compile(REQUEST_FAILURE_PATTERN);
        resultFileScanner = new Scanner(file);
        BiMap<String, String> errorMessageMap = HashBiMap.create();
        String res = null;
        Map<String, Map<String, Integer>> testMap = new HashMap<String, Map<String, Integer>>();
        while ((res = resultFileScanner.findWithinHorizon(errorPattern, 0)) != null) {
            String errorNamePattern = "(?<=rc=)['\"](.*?)['\"]";
            String errorMessagePattern = "(?<=rm=)['\"](.*?)['\"]";
            String errorTestPattern = "(?<=lb=)['\"](.*?)['\"]";
            Matcher errorCountMatcher = Pattern.compile(errorNamePattern)
                    .matcher(res);
            Matcher errorMessageMatcher = Pattern.compile(errorMessagePattern)
                    .matcher(res);
            Matcher errorTestMatcher = Pattern.compile(errorTestPattern)
                    .matcher(res);
            String errorName = null;

            if (errorTestMatcher.find()) {
                String group = errorTestMatcher.group();
                Map<String, Integer> errorCountMap = testMap.get(group);
                if (errorCountMatcher.find()) {
                    errorName = errorCountMatcher.group();
                    if (errorCountMap == null) {
                        errorCountMap = new HashMap<String, Integer>();
                        testMap.put(group, errorCountMap);
                    }
                    Integer errorCount = errorCountMap.get(errorName);
                    if (errorCount == null) {
                        errorCount = 0;
                        if (errorMessageMatcher.find()) {
                            String errorMessage = errorMessageMatcher.group();
                            errorMessageMap.put(errorName, errorMessage);
                        }
                    }
                    errorCount++;
                    errorCountMap.put(errorName, errorCount);
                }
            }

            Count++;
        }
        int rowNum = 0;
        int index = 1;
        CreationHelper helper = workBook.getCreationHelper();
        Sheet sheet = workBook.createSheet("ErrorSheet");
        Row row1 = sheet.createRow((short) rowNum++);

        CellStyle cellStyle1 = workBook.createCellStyle();
        Font timesBoldFont1 = workBook.createFont();
        timesBoldFont1.setFontName("Cambria");
        cellStyle1.setFont(timesBoldFont1);
        cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        timesBoldFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle1.setFont(timesBoldFont1);
        cellStyle1.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyle1.setWrapText(true);
        cellStyle1.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle1.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle1.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle1.setBorderTop(CellStyle.BORDER_THIN);
        Cell createCell1 = row1.createCell(0);
        createCell1.setCellStyle(cellStyle1);
        createCell1.setCellValue("Requests");
        for (String error : errorMessageMap.keySet()) {

            sheet.setDisplayGridlines(true);
            CellStyle cellStyle = workBook.createCellStyle();
            Font timesBoldFont = workBook.createFont();
            timesBoldFont.setFontName("Cambria");
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            cellStyle.setWrapText(true);
            cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
            cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            cellStyle.setBorderTop(CellStyle.BORDER_THIN);
            Cell createCell = row1.createCell(index++);
            createCell.setCellStyle(cellStyle);
            RichTextString createRichTextString = helper
                    .createRichTextString(errorMessageMap.get(error));
            createCell.setCellValue(createRichTextString.toString());
        }
        for (int colNum = 0; colNum < row1.getLastCellNum(); colNum++) {
            sheet.autoSizeColumn(colNum);
        }
        if (errorMessageMap.size() == 0) {
            row1.setHeight((short) 100);
            CellStyle cellStyle = workBook.createCellStyle();
            Font timesBoldFont = workBook.createFont();
            timesBoldFont.setFontName("Cambria");
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            cellStyle.setWrapText(true);
            cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
            cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            Cell createCell = row1.createCell(3);
            createCell.setCellValue("No errors in jtl file  ");
            createCell.setCellStyle(cellStyle);
            for (int colNum = 0; colNum < row1.getLastCellNum(); colNum++) {
                sheet.autoSizeColumn(colNum);
            }
            return index;
        }
        for (String testName : testMap.keySet()) {
            index = 0;
            Row row = sheet.createRow((short) rowNum++);
            sheet.setDisplayGridlines(true);
            CellStyle cellStyle = workBook.createCellStyle();
            Font timesBoldFont = workBook.createFont();
            timesBoldFont.setFontName("Cambria");
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(timesBoldFont);
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
            cellStyle.setWrapText(true);
            cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
            cellStyle.setBorderRight(CellStyle.BORDER_THIN);
            cellStyle.setBorderTop(CellStyle.BORDER_THIN);
            Cell createCell = row.createCell(index++);
            createCell.setCellStyle(cellStyle);
            RichTextString createRichTextString = helper
                    .createRichTextString(testName);
            createCell.setCellValue(createRichTextString.toString());
            Iterator<Cell> cellIterator = row1.cellIterator();
            Cell cell = cellIterator.next();
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                String string = errorMessageMap.inverse().get(cell.toString());
                Map<String, Integer> map = testMap.get(testName);
                Integer integer = map.get(string);
                if (integer == null) {
                    integer = 0;
                }
                createCell = row.createCell(cell.getColumnIndex());
                createRichTextString = helper
                        .createRichTextString(integer + "");
                createCell.setCellValue(createRichTextString.toString());
                cellStyle = workBook.createCellStyle();
                cellStyle.setFont(timesBoldFont);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                timesBoldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(timesBoldFont);
                cellStyle
                        .setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                cellStyle.setWrapText(true);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                createCell.setCellStyle(cellStyle);
            }
            for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                sheet.autoSizeColumn(colNum);
            }


        }

        return Count;
    }

    protected static StringBuilder getWorkBookData(Workbook workBook) {
        Iterator<Row> rowIterator = workBook.getSheet("TestSummary").iterator(); // Create

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><body><table border='1'>");
        int tempRo = 0;
        String isMemCpu = properties.getProperty("isMemCpu");
        while (rowIterator.hasNext()) {
            if (tempRo == 0) {
                stringBuilder
                        .append("<tr style='background:yellow;font-weight:bold'>");

            } else if (isMemCpu.equalsIgnoreCase("true")) {
                if (tempRo == 1) {
                    stringBuilder
                            .append("<tr style='background:TURQUOISE;font-weight:bold'>");

                } else if (tempRo == 11 || tempRo == 12) {
                    stringBuilder
                            .append("<tr style='background:lightblue;font-weight:bold'>");

                } else if (tempRo >= 14) {

                    stringBuilder
                            .append("<tr style='background:white;'>");
                } else if (tempRo >= 2 && tempRo < 11) {
                    stringBuilder
                            .append("<tr style='background:white;'>");
                }

            } else {
                if (tempRo == 1) {
                    stringBuilder
                            .append("<tr style='background:TURQUOISE;font-weight:bold'>");

                } else if (tempRo == 8 || tempRo == 9) {
                    stringBuilder
                            .append("<tr style='background:lightblue;font-weight:bold'>");

                } else if (tempRo >= 11) {

                    stringBuilder
                            .append("<tr style='background:white;'>");
                } else if (tempRo >= 2 && tempRo < 8) {
                    stringBuilder
                            .append("<tr style='background:white;'>");
                }

            }
            tempRo++;
            Row row = rowIterator.next(); // Read Rows from Excel document
            Iterator<Cell> cellIterator = row.cellIterator();// Read every
            // column for
            // every row
            // that is READ
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); // Fetch CELL
                switch (cell.getCellType()) { // Identify CELL type
                    case Cell.CELL_TYPE_NUMERIC:
                        System.out.print("<td>" + cell.getNumericCellValue()
                                + "</td>");
                        // print numeric value
                        stringBuilder.append("<td>" + cell.getNumericCellValue()
                                + "</td>");
                        break;
                    case Cell.CELL_TYPE_STRING:
                        System.out.print("<td>" + cell.getStringCellValue()
                                + "</td>"); // print string value
                        stringBuilder.append("<td>" + cell.getStringCellValue()
                                + "</td>");
                        break;
                }
            }
            stringBuilder.append("</tr>");
            System.out.println("</tr>"); // To iterate over to the next row
        }
        stringBuilder.append("</table></body></html>");

        return stringBuilder;
    }



    public Date getSqlTime()
    {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        java.sql.Date d=new java.sql.Date(calendar.getTime().getTime());

        return date;
    }

    /*

    public String getAWSTag()
    {
        String publicIp = getHttpResponse("http://169.254.169.254/latest/meta-data/public-ipv4");
        String instanceId = getHttpResponse("http://169.254.169.254/latest/meta-data/instance-id");
        String availabilityZone = getHttpResponse("http://169.254.169.254/latest/meta-data/placement/availability-zone");
        String instanceType = getHttpResponse("http://169.254.169.254/latest/meta-data/instance-type");
        String userData = getHttpResponse("http://169.254.169.254/latest/user-data/");
        HashMap<String, String> userDataMap = new HashMap<>();

        // this data may not be encoded wonderfully, try/catch just in case its missing or bad
        try {
            byte[] decoded = Base64.decodeBase64(userData);;
            String userDataDecodedString = new String(decoded, "UTF-8").replace("\n", "");
            String[] userDataSplit = userDataDecodedString.split(",");
            for( String s: userDataSplit ){
                userDataMap.put(s.split(":")[0], s.split(":")[1]);
            }
        } catch (Exception e){
            // if we add keys to userdata, their default values should be specified here
            userDataMap.put("gitRev","error");
            userDataMap.put("hostname","error");
        }
    }
*/


}

