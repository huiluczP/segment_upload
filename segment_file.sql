CREATE TABLE `segment_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_path` varchar(200) NOT NULL,
  `file_name` varchar(200) DEFAULT NULL,
  `size` int DEFAULT NULL,
  `segment_index` int DEFAULT NULL,
  `segment_size` int DEFAULT NULL,
  `segment_total` int DEFAULT NULL,
  `md5_key` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
)