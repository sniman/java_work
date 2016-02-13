DROP TABLE IF EXISTS `sample`.`customer`;
CREATE TABLE  `sample`.`customer` (
  `customer_id` int(11) NOT NULL,
  `discount_code` char(1) NOT NULL,
  `zip` varchar(10) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `addressline1` varchar(30) DEFAULT NULL,
  `addressline2` varchar(30) DEFAULT NULL,
  `city` varchar(25) DEFAULT NULL,
  `state` char(2) DEFAULT NULL,
  `phone` char(12) DEFAULT NULL,
  `fax` char(12) DEFAULT NULL,
  `email` varchar(40) DEFAULT NULL,
  `credit_limit` int(11) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  KEY `FOREIGNKEY_discount_code` (`discount_code`),
  KEY `FOREIGNKEY_zip` (`zip`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`discount_code`) REFERENCES `discount_code` (`discount_code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_ibfk_2` FOREIGN KEY (`zip`) REFERENCES `micro_market` (`zip_code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;