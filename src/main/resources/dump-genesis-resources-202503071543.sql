-- MySQL dump 10.13  Distrib 9.0.1, for macos14.7 (arm64)
--
-- Host: localhost    Database: genesis-resources
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `positions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `position_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb6lkwasxdrfpxihi038w6ixt6` (`position_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `positions`
--

LOCK TABLES `positions` WRITE;
/*!40000 ALTER TABLE `positions` DISABLE KEYS */;
INSERT INTO `positions` VALUES (1,'accountant'),(3,'cleaner'),(4,'director'),(2,'economist'),(7,'engineer'),(8,'plumber'),(10,'webPositionTest');
/*!40000 ALTER TABLE `positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_position`
--

DROP TABLE IF EXISTS `user_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_position` (
  `user_id` bigint NOT NULL,
  `position_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`position_id`),
  KEY `FKf749itob163tyetmpubyaoctn` (`position_id`),
  CONSTRAINT `FKf749itob163tyetmpubyaoctn` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`),
  CONSTRAINT `FKjejtnfxloo8rkxgcif5trracr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_position`
--

LOCK TABLES `user_position` WRITE;
/*!40000 ALTER TABLE `user_position` DISABLE KEYS */;
INSERT INTO `user_position` VALUES (1,1),(2,1),(5,1),(7,1),(11,1),(5,2),(7,2),(9,2),(2,3),(7,3),(9,3),(11,3),(12,3),(1,4),(2,4),(5,4),(9,4),(11,4),(12,4),(7,7),(7,8),(2,10),(7,10);
/*!40000 ALTER TABLE `user_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `birth_date` date DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `person_id` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK97ih1g5lcdf1s3fg7oo4e18jw` (`person_id`),
  UNIQUE KEY `UK6km2m9i3vjuy36rnvkgj1l61s` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'1971-06-28','Elon','yB9fR6tK0wLm','Musk','cfebb8a9-035b-4cf8-8d66-8166ecd2f16d'),(2,'1990-01-15','Marie','jXa4g3H7oPq2','Miller','b1e82b97-9bc4-406c-a77a-b72f44d3ff4c'),(5,'1979-09-20','Jan','cN1vZ8pE5sYx','Novak','8697f837-ed1b-40f0-988c-2595893150b6'),(7,'1985-03-20','Lenka','tQdG2kP3mJfB','Novakova','0e68f1f6-628c-4d6d-a427-14a7c2a40af8'),(9,'1985-03-20','Petra','iM5sO6zXcW7v','Nemcova','f5243ccf-7ef1-4437-ab94-5d229c235250'),(10,'1985-03-20','Api','mY6sT1jA3cLz','Test','ba479d59-acf4-4e5c-990a-6fa4c377d99d'),(11,'1994-01-03','Web','nS7tJ0qR5wGh','Webtest','6c1d6363-7155-4135-bf37-1b8433e5b81e'),(12,'2000-01-01','Web2','dW9pL2eU1yNc','WebTest2','ef0ac70b-a52f-47ac-bba0-10c0f6d5415e');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'genesis-resources'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-07 15:43:52
