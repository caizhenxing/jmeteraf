package com.qsa.jaf;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ${"UMAMAHESH.G"} on 11/03/15.
 */
public class CsvJDBCInsert {

    public static Logger logger = Logger.getLogger(CsvJDBCInsert.class.getName());

    public Connection getConnection(String serverIP, String username, String password,String dbName)    {

        String DB_URL = "jdbc:mysql://"+serverIP+":3306/" + dbName;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");

            logger.info("Connecting to a MySQL database...");
            conn = DriverManager.getConnection(DB_URL, username, password);
            if(conn!=null)
            {
                logger.info("Connected database successfully...");
            }


        } catch (ClassNotFoundException cn) {
           logger.error("Exception driver class was not found: " + cn.getMessage());
        } catch (SQLException se) {
            logger.error("Exception while connecting to the database : " + se.getMessage());
        } catch (NullPointerException e) {
            logger.error("Exception null object is present: " + e.getMessage());
        }catch (Exception e) {
            logger.error("Exception while creating a connection: " + e.getMessage());
        }

        return conn;
    }

    public PreparedStatement statement (Connection connection, String sql)    {

        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(sql);
        } catch (SQLException e) {
            logger.error("Exception raised while preparing a statement" + e.getMessage());
        }
        return stmt;
    }

    public void insertAggregatecsvData(PreparedStatement preparedStatement, String csvFile, int lastvalue)    {

         try {

            String csvPath = csvFile;
            Reader fileReader = (Reader) new FileReader(csvPath);
            CSVReader reader = new CSVReader(fileReader);
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null)
            {

                String datetime = line[0];
                String responsetime = line[1];
                String samplername = line[2];
                String httpreturncode = line[3];
                String statusmessage = line[4];
                String threadgroup = line[5];
                String messagetype = line[6];
                String condition = line[7];
                String Bytes = line[8];
                String activeThreads = line[9];
                String noofThreads = line[10];
                String latency = line[11];



                preparedStatement.setTimestamp(1, Timestamp.valueOf(datetime));
                preparedStatement.setString(2, samplername);
                preparedStatement.setInt(3, Integer.parseInt(responsetime));
                preparedStatement.setString(4, httpreturncode);
                preparedStatement.setString(5, statusmessage);
                preparedStatement.setString(6, threadgroup);
                preparedStatement.setString(7, messagetype);
                preparedStatement.setString(8, condition);
                preparedStatement.setInt(9, Integer.parseInt(Bytes));
                preparedStatement.setInt(10, Integer.parseInt(activeThreads));
                preparedStatement.setInt(11, Integer.parseInt(noofThreads));
                preparedStatement.setInt(12, Integer.parseInt(latency));
                preparedStatement.setInt(13, lastvalue);

                preparedStatement.addBatch();
           }
             preparedStatement.executeBatch();
         } catch (FileNotFoundException e) {
             logger.error("File not found in the results directory : " + e.getMessage());
         } catch (IOException e) {
             logger.error("File IO Exception due to " + e.getMessage());
         } catch (SQLException e) {
             logger.error("Sql execution exception " + e.getMessage());
         } catch (Exception e) {
             logger.error("Exception while inserting data into tables" + e.getMessage());
         }



    }

    public void insertAggregateData(PreparedStatement preparedStatement, String csvFile, int lastvalue)    {

        try {

            String csvPath = csvFile;
            Reader fileReader = (Reader) new FileReader(csvPath);
            CSVReader reader = new CSVReader(fileReader);
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null)
            {

                String agg_sampler_name = line[0];
                String agg_report_count = line[1];
                String agg_average_response_time = line[2];
                String agg_report_median = line[3];
                String agg_report_90_line = line[4];
                String aggregate_report_min = line[5];
                String agg_report_max = line[6];
                String agg_report_error_percent = line[7];
                String agg_report_rate = line[8];
                String agg_report_bandwidth = line[9];
                String agg_report_stddev = line[10];


                preparedStatement.setString(1, agg_sampler_name);
                preparedStatement.setInt(2, Integer.parseInt(agg_report_count));
                preparedStatement.setInt(3, Integer.parseInt(agg_average_response_time));
                preparedStatement.setInt(4, Integer.parseInt(agg_report_median));
                preparedStatement.setInt(5, Integer.parseInt(agg_report_90_line));
                preparedStatement.setInt(6, Integer.parseInt(aggregate_report_min));
                preparedStatement.setInt(7, Integer.parseInt(agg_report_max));
                preparedStatement.setDouble(8, Double.parseDouble(agg_report_error_percent));
                preparedStatement.setString(9, agg_report_rate);
                preparedStatement.setDouble(10, Double.parseDouble(agg_report_bandwidth));
                preparedStatement.setDouble(11, Double.parseDouble(agg_report_stddev));
                preparedStatement.setInt(12, lastvalue);


                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (FileNotFoundException e) {
            logger.error("File not found in the results directory : " + e.getMessage());
        } catch (IOException e) {
            logger.error("File IO Exception due to " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Sql execution exception " + e.getMessage());
        } catch (Exception e) {
        logger.error("Exception while inserting data into tables" + e.getMessage());
    }


    }

    public Integer insertQueryGetId(Connection connection,String query) {
        Integer risultato=-1;

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()){
                risultato=rs.getInt(1);
            }
            rs.close();

            stmt.close();
        } catch (Exception e) {
            logger.error("Exception raised while preparing a statement" + e.getMessage());
            risultato=-1;
        }
        return risultato;
    }


    public java.sql.Timestamp getCurrentDatetime() {
        java.util.Date today = new java.util.Date();

        return new java.sql.Timestamp(today.getTime());
    }

    public Integer insertRunDetailsAndGetId(Connection connection, String sql,String runName, String description)
    {
        Integer risultato=-1;
        PreparedStatement ps = null;



        try {

            java.sql.Timestamp date = getCurrentDatetime();

            ps =  connection.prepareStatement(sql,ps.RETURN_GENERATED_KEYS);

            ps.setInt(1,0);
            ps.setString(2, runName);
            ps.setString(3, description);
            ps.setTimestamp(4, date);
            ps.setString(5, "Test Started");

            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                risultato=rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
           logger.error("Not able to get the auto generated from runs table" +e.getMessage());
            risultato=-1;
        }
        return risultato;
    }


    public void updateStatusinRunDetails(Connection connection, String sql)
    {
         PreparedStatement ps = null;

        try {

            ps = connection.prepareStatement(sql);
            ps.executeUpdate(sql);
            logger.info("Data updated sucessfully");
            ps.close();
        } catch (Exception e) {
            logger.error("Not able to update runs table status" +e.getMessage());

        }

    }



    public void insertErrorsCsvData(PreparedStatement preparedStatement, String csvFile, int lastvalue)    {

        try {

            String csvPath = csvFile;
            Reader fileReader = (Reader) new FileReader(csvPath);
            CSVReader reader = new CSVReader(fileReader);
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null)
            {

                String time_stamp = line[0];
                String elapsed_time = line[1];
                String sampler_name = line[2];
                String response_code = line[3];
                String response_message = line[4];
                String thread_name = line[5];
                String data_type = line[6];
                String success_code = line[7];
                String bytes = line[8];
                String group_threads = line[9];
                String num_threads = line[10];
                String latency = line[11];
                String sample_count=line[12];
                String error_count=line[13];

                preparedStatement.setTimestamp(1, Timestamp.valueOf(time_stamp));
                preparedStatement.setString(2, elapsed_time);
                preparedStatement.setString(3, sampler_name);
                preparedStatement.setString(4, response_code);
                preparedStatement.setString(5, response_message);
                preparedStatement.setString(6, thread_name);
                preparedStatement.setString(7, data_type);
                preparedStatement.setString(8, success_code);
                preparedStatement.setInt(9, Integer.parseInt(bytes));
                preparedStatement.setInt(10, Integer.parseInt(group_threads));
                preparedStatement.setInt(11, Integer.parseInt(num_threads));
                preparedStatement.setInt(12, Integer.parseInt(latency));
                preparedStatement.setInt(13, Integer.parseInt(sample_count));
                preparedStatement.setInt(14, Integer.parseInt(error_count));
                preparedStatement.setInt(15, lastvalue);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (FileNotFoundException e) {
            logger.error("File not found in the results directory : " + e.getMessage());
        } catch (IOException e) {
            logger.error("File IO Exception due to " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Sql execution exception " + e.getMessage());
        } catch (Exception e) {
            logger.error("Exception while inserting data into tables" + e.getMessage());
        }



    }

    public void insertRunsData(PreparedStatement preparedStatement, String csvFile, int lastvalue, int noofusers,int duration)    {

        try {

            String csvPath = csvFile;
            Reader fileReader = (Reader) new FileReader(csvPath);
            CSVReader reader = new CSVReader(fileReader);
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null)
            {

                String no_of_requests = line[1];
                String avg_response_time = line[2];
                String throughput = line[3];
                String throughput_kb = line[4];
                String error_percent = line[4];


                preparedStatement.setInt(1, noofusers);
                preparedStatement.setInt(2, Integer.parseInt(no_of_requests));
                preparedStatement.setInt(3, Integer.parseInt(avg_response_time));
                preparedStatement.setDouble(4, Double.parseDouble(throughput));
                preparedStatement.setDouble(5, Double.parseDouble(throughput_kb));
                preparedStatement.setDouble(6, Double.parseDouble(error_percent));
                preparedStatement.setInt(7, duration);
                preparedStatement.setInt(8, lastvalue);


                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (FileNotFoundException e) {
            logger.error("File not found in the results directory : " + e.getMessage());
        } catch (IOException e) {
            logger.error("File IO Exception due to " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Sql execution exception " + e.getMessage());
        } catch (Exception e) {
            logger.error("Exception while inserting data into tables" + e.getMessage());
        }

    }


    public String convertTime(String datetime)
    {
        java.util.Date dt = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
       return dateFormat.format(cal.getTime());
    }



    }

