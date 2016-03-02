import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import com.qsa.jaf.CsvJDBCInsert;
import com.qsa.jaf.UtilityFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.LogManager;

import static org.apache.commons.io.FileUtils.*;
import static org.apache.log4j.PropertyConfigurator.*;


/**
 * Created by ${"UMAMAHESH.G"} on 11/03/15.
 */
public class RunTests extends UtilityFunctions {


    private static String filePropertiesName = System.getProperty("user.dir") + "//perfauto.properties";
    private static String logPropertiesName = System.getProperty("user.dir") + "//log4j.properties";

    public static Logger logger = Logger.getLogger(RunTests.class.getName());


    public RunTests()
    {
        System.setProperty("PerfAutomation.log", System.getProperty("user.dir")+"//logs//PerfAutomation.log");

    }



    public void RunTestAPI(File JmxDirectory) throws IOException {
        String status;
        String documentTitle;
        File currentResultDir;
        Connection connection = null;
        PreparedStatement statement = null;
        CsvJDBCInsert insert = null;
        Integer integer = null;

        try {

              logger.info("JMX Directory : " + JmxDirectory.getAbsolutePath());



            File[] files = JmxDirectory.listFiles(new FilenameFilter()
            {
                public boolean accept(File JmxDirectory, String name) {
                    return name.toLowerCase().endsWith(".jmx");
                }
            });

            if (files.length == 0)
            {
                logger.error("No jmx files are found in the directory : " + JmxDirectory.getAbsolutePath());
            }

            for (File file : files)
            {
                documentTitle =  FilenameUtils.removeExtension(getproductname()+"_"+gettestplanname()+"_"+getreleasenum()+"_"+getbuildnum()+"_"+file.getName()) + "_" + properties.getProperty("documentTitle");

                if (documentTitle == null) {
                    documentTitle = "Not able to read properties file";
                }

                logger.info(documentTitle + " : Execution Started");

                if (new File(getresultsdir()).exists() && !skipTests()) {

                    try
                    {
                        cleanDirectory(new File(getresultsdir()));
                    }
                    catch (Exception ex)
                    {

                        logger.error("Not able to delete files in the results folder... going for force delete : " +ex.getMessage());
                        forceDeleteOnExit(new File(getresultsdir()));
                        forceDeleteOnExit(new File(getjmetererrorsummarydir()));
                        forceDeleteOnExit(new File(getjmeterdetailsummarydir()));
                    }


                }

                currentResultDir = createDirectory(FilenameUtils.removeExtension(file.getName()), getbuildnum(), getresultsdir());
                logger.info("Result directory:"+currentResultDir);

                logger.info("Executing test: " + file.getName());

                String sqlRun = "INSERT INTO "+properties.getProperty("dbschemaname")+".perf_run_details " + "VALUES (?,?,?,?,?)";

                insert = new CsvJDBCInsert();
                connection = insert.getConnection(properties.getProperty("dbipaddress"),properties.getProperty("dbusername"), properties.getProperty("dbpassword"),properties.getProperty("dbschemaname"));

                if (!(connection == null))
                {
                    statement = insert.statement(connection, sqlRun);
                    logger.info("Inserting data into perf_run_details table");
                    integer = insert.insertRunDetailsAndGetId(connection,sqlRun,FilenameUtils.getBaseName(file.getName()), documentTitle);
                    logger.info(FilenameUtils.getBaseName(file.getName()) +" performance Run ID : "+integer);
                }

                if (!skipTests())
                {

                    runJmeterProcess(getjmeterbindir(), JmxDirectory + "//" + file.getName(), "-j", currentResultDir + "//" + FilenameUtils.getBaseName(file.getName())+ ".log", "-l", currentResultDir+"//"+FilenameUtils.getBaseName(file.getName())+".jtl");

                } else {
                    logger.info("The jMeter tests has been skipped");
                }

                if (!skipReports()) {

                    logger.info("-------------------------------------------------------");
                    logger.info(" GENERATING JMETER TEST REPORTS ");
                    logger.info("-------------------------------------------------------");


                if (!skipAggregateReports()) {

                        logger.debug("Result directory:" + currentResultDir.getAbsolutePath());
                        logger.debug("Ext directory:" + getjmeterextdir());
                        generateAggregateReports(currentResultDir.getAbsolutePath(), getjmeterextdir(), "csv", currentResultDir.getAbsolutePath(), "AggregateReport");
                        generatepngreports(currentResultDir.getAbsolutePath(), getjmeterextdir(), currentResultDir.getAbsolutePath());

                        try {

                            String sql = "INSERT INTO " +properties.getProperty("dbschemaname")+".perf_run_results " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                            String sqljtl = "INSERT INTO "+properties.getProperty("dbschemaname")+".perf_run_requests_summary " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                            String sqljtlERRORS = "INSERT INTO "+properties.getProperty("dbschemaname")+".perf_run_errors " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


                            if (integer!=-1)
                            {

                                logger.info("Inserting data into perf_run_results table ");
                                statement = insert.statement(connection, sql);
                                logger.info("Result directory:" + currentResultDir + "//" + FilenameUtils.getBaseName(file.getName()) + ".csv");
                                insert.insertAggregateData(statement, currentResultDir + "//" + FilenameUtils.getBaseName(file.getName()) + ".csv", integer);


                                if(new File(getjmeterdetailsummarydir() + "//" + "AggregateCSVAll.csv").exists()) {
                                    logger.info("Inserting data into perf_run_requests_summary table ");
                                    statement = insert.statement(connection, sqljtl);
                                    logger.info("Result directory:" + getjmeterdetailsummarydir() + "//" + "AggregateCSVAll" + ".csv");
                                    insert.insertAggregatecsvData(statement, getjmeterdetailsummarydir() + "//" + "AggregateCSVAll" + ".csv", integer);
                                }
                                else
                                {
                                    logger.error("The "+getjmeterdetailsummarydir() + "//" + "AggregateCSVAll.csv" +" file is not present in the results directory. Please check the jmx file whether results path added properly or not ");
                                }

                                if(new File(getjmetererrorsummarydir() + "//" + "Errors.csv").exists())
                                {

                                    logger.info("Inserting data into perf_run_errors table ");
                                    statement = insert.statement(connection, sqljtlERRORS);
                                    logger.info("Result directory:" + getjmetererrorsummarydir() + "//" + "Errors" + ".csv");
                                    insert.insertErrorsCsvData(statement, getjmetererrorsummarydir() + "//" + "Errors" + ".csv", integer);
                                }
                                else
                                {
                                    logger.error("The "+getjmetererrorsummarydir() + "//" + "Errors.csv"+"file is not present in the results directory. Please check the jmx file whether results path added properly or not");
                                }
                            }

                        }
                        catch (Exception ex)
                        {
                            logger.error("Inserting to the database has been failed:" + ex.getMessage());
                            status = "Failed";
                            String sql = "update perf_run_details SET status='"+status+"' where id="+ integer;
                            insert.updateStatusinRunDetails(connection,sql);


                            try {

                                if (statement != null)
                                    connection.close();
                            } catch (SQLException se) {
                                logger.error(se.getMessage());
                            }
                        }


                    }


                    else {
                        logger.info("The jmeter Aggregate reports has been skipped");
                    }


                } else {
                    logger.info("The jmeter test reports generation has been skipped ");
                }


                if (currentResultDir.exists()) {
                    logger.info("Archiving results to master results folder : " + getresultsmasterdir());

                    if(new File(getjmetererrorsummarydir()) .exists() && new File(getjmeterdetailsummarydir()).exists()) {
                        copyDirectory(new File(getjmeterdetailsummarydir()), currentResultDir);
                        copyDirectory(new File(getjmetererrorsummarydir()), currentResultDir);
                        FileUtils.copyDirectoryToDirectory(currentResultDir, new File(getresultsmasterdir()));
                    }
                }


                logger.info(" ");
                logger.info(file.getName() + " test has been completed");
                logger.info(" ");
                logger.info(" -----------------------------------------");
                logger.info(" ");

                status = "Passed";
                String sql = "update perf_run_details SET status='"+status+"' where id="+ integer;
                insert.updateStatusinRunDetails(connection,sql);


            }


        }
        catch (Exception ex)
        {
            status = "Failed";
            String sql = "update perf_run_details SET status="+status+" where id="+ integer;
            insert.updateStatusinRunDetails(connection,sql);
            logger.error(status + ex.getMessage());



        } finally {
            logger.info("Completed Jmeter Performance Test");
            LogManager.getLogManager().reset();
        }
    }

    @Test
    public void executeJMeterTest() throws IOException {
        RunTests testManager = new RunTests();

        try
        {
            properties = new Properties();
            properties.load(new FileInputStream(filePropertiesName));
            configure(logPropertiesName);

        } catch (NullPointerException ex)
        {
            logger.error("Property file is missing in the framework : " + ex.getMessage());
        } catch (IOException ex)
        {
            logger.error("Not able to read property file : " + ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("Not able to read property file : " + ex.getMessage());
        }

        logger.info(" ");
        logger.info("-------------------------------------------------------");
        logger.info(" P E R F O R M A N C E    T E S T S");
        logger.info("-------------------------------------------------------");


        File file = new File(getjmxdir());
        File files[] = file.listFiles();

        if(!(files==null)) {

            for (File directory : files) {
                if (directory.isDirectory()) {
                    testManager.RunTestAPI(directory);
                    logger.info(" ");
                    logger.info("-------------------------------------------------------");
                }
                else if (directory.isFile() && FilenameUtils.getExtension(String.valueOf(directory)).equalsIgnoreCase("jmx") )
                {
                       testManager.RunTestAPI(file);
                        logger.info(" ");
                        logger.info("-------------------------------------------------------");

                } else {
                    logger.info("No jmx files and directories are present in the Directory : ");
                    logger.info(" ");
                    logger.info("-------------------------------------------------------");
                }

            }
        }
        else
        {
            logger.error(getjmxdir()+ " directory does not contain any jmx files");
        }
    }

}
