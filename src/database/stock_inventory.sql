
CREATE SCHEMA IF NOT EXISTS `stock_inventory`;
USE `stock_inventory`;

CREATE TABLE IF NOT EXISTS `stock` (
  `stock_id` int(11) NOT NULL AUTO_INCREMENT,
  `cat_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `old_supply` int(150) DEFAULT '0',
  `new_supply` int(150) NOT NULL,
  `quantity` int(50) NOT NULL,
  `rate` decimal(10,0) NOT NULL,
  `unit` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL,
  PRIMARY KEY (`stock_id`),
  UNIQUE KEY `item_name_UNIQUE` (`item_name`),
  KEY `fk_cat` (`cat_id`),
  CONSTRAINT `fk_cat` 
  FOREIGN KEY (`cat_id`) 
  REFERENCES `category` (`cat_id`) 
  ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `category` (
  `cat_id` int(11) NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(255) NOT NULL,
  `cat_description` text,
  PRIMARY KEY (`cat_id`),
  UNIQUE KEY `cat_name_UNIQUE` (`cat_name`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `sales` (
  `sales_id` int(11) NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `quantity` int(50) NOT NULL,
  `rate` int(50) NOT NULL,
  `unit` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL,
  PRIMARY KEY (`sales_id`)
) ENGINE=InnoDB;