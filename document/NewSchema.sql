CREATE TABLE `perf_run_details` ( `id` int(11) NOT NULL AUTO_INCREMENT, 
`build_tag` varchar(45) DEFAULT NULL,  `description` varchar(250) DEFAULT NULL,  `date_time` datetime DEFAULT NULL,  
`status` varchar(45) DEFAULT NULL,  PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;


CREATE TABLE `perf_run_results` (
  `agg_sampler_name` varchar(250) NOT NULL,
  `agg_no_of_requests` int(11) DEFAULT NULL,
  `agg_avg_response_time_ms` int(11) DEFAULT NULL,
  `agg_report_median` int(11) DEFAULT NULL,
  `agg_report_90%_line` int(11) DEFAULT NULL,
  `agg_report_min_ms` int(11) DEFAULT NULL,
  `agg_report_max_ms` int(11) DEFAULT NULL,
  `agg_report_error_percent` varchar(100) DEFAULT NULL,
  `agg_report_throughput` double DEFAULT NULL,
  `agg_report_throughput_kbs)` double DEFAULT NULL,
  `agg_report_stddev` double DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  KEY `FK_Perf_Runs_ID_id` (`id`),
  CONSTRAINT `Perf_Runs_ID_FK` FOREIGN KEY (`id`) REFERENCES `perf_run_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `perf_run_requests_summary` (
  `detail_date_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `detail_sampler_name` varchar(250) DEFAULT NULL,
  `detail_response_time` int(25) DEFAULT NULL,
  `detail_http_return_code` varchar(500) DEFAULT NULL,
  `detail_status_message` varchar(800) DEFAULT NULL,
  `detail_thread_group_name` varchar(250) DEFAULT NULL,
  `detail_message_type` varchar(250) DEFAULT NULL,
  `detail_condition` varchar(45) DEFAULT NULL,
  `detail_Bytes_kb` int(50) DEFAULT NULL,
  `detail_active_threads` int(25) DEFAULT NULL,
  `detail_no_of_threads` int(25) DEFAULT NULL,
  `detail_latency` int(25) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  KEY `Perf_Runs_detail_FK_id` (`id`),
  CONSTRAINT `Perf_Runs_detail_FK` FOREIGN KEY (`id`) REFERENCES `perf_run_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `perf_run_errors` (
  `date_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `elapsed_time` varchar(45) DEFAULT NULL,
  `sampler_name` varchar(500) DEFAULT NULL,
  `response_code` varchar(500) DEFAULT NULL,
  `response_message` varchar(800) DEFAULT NULL,
  `thread_name` varchar(500) DEFAULT NULL,
  `data_type` varchar(45) DEFAULT NULL,
  `success_code` varchar(45) DEFAULT NULL,
  `bytes` int(11) DEFAULT NULL,
  `group_threads` int(11) DEFAULT NULL,
  `num_threads` int(11) DEFAULT NULL,
  `latency` int(11) DEFAULT NULL,
  `sample_count` int(11) DEFAULT NULL,
  `error_count` int(11) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  KEY `FK_Perf_Runs_Errors_idx` (`id`),
  CONSTRAINT `FK_Perf_Runs_Errors` FOREIGN KEY (`id`) REFERENCES `perf_run_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
