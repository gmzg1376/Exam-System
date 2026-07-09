/*
 Navicat Premium Dump SQL

 Source Server         : 123456
 Source Server Type    : MySQL
 Source Server Version : 50173 (5.1.73-community)
 Source Host           : localhost:3306
 Source Schema         : exam_db

 Target Server Type    : MySQL
 Target Server Version : 50173 (5.1.73-community)
 File Encoding         : 65001

 Date: 08/07/2026 19:15:07
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer_detail
-- ----------------------------
DROP TABLE IF EXISTS `answer_detail`;
CREATE TABLE `answer_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sheet_id` bigint(20) NULL DEFAULT NULL,
  `question_id` bigint(20) NULL DEFAULT NULL,
  `user_answer` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `is_correct` tinyint(1) NULL DEFAULT 0,
  `spend_time` bigint(20) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sheet_id`(`sheet_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  CONSTRAINT `answer_detail_ibfk_1` FOREIGN KEY (`sheet_id`) REFERENCES `answer_sheet` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `answer_detail_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of answer_detail
-- ----------------------------
INSERT INTO `answer_detail` VALUES (1, 1, 1, 'A', 1, 8, '2026-07-07 19:06:34');
INSERT INTO `answer_detail` VALUES (2, 1, 2, '支撑', 0, 13, '2026-07-07 19:06:34');
INSERT INTO `answer_detail` VALUES (3, 1, 3, '单一职责', 1, 0, '2026-07-07 19:06:34');
INSERT INTO `answer_detail` VALUES (4, 2, 5, 'C', 1, 9, '2026-07-07 20:01:03');
INSERT INTO `answer_detail` VALUES (5, 2, 6, 'A', 1, 2, '2026-07-07 20:01:03');
INSERT INTO `answer_detail` VALUES (6, 2, 7, '分治', 1, 0, '2026-07-07 20:01:03');
INSERT INTO `answer_detail` VALUES (7, 3, 8, 'C', 1, 7, '2026-07-07 20:11:58');
INSERT INTO `answer_detail` VALUES (8, 3, 9, 'A', 1, 3, '2026-07-07 20:11:58');
INSERT INTO `answer_detail` VALUES (9, 3, 10, 'A,B,D', 1, 0, '2026-07-07 20:11:58');
INSERT INTO `answer_detail` VALUES (10, 4, 11, 'C', 1, 4, '2026-07-08 13:42:41');
INSERT INTO `answer_detail` VALUES (11, 4, 12, 'D', 1, 4, '2026-07-08 13:42:41');
INSERT INTO `answer_detail` VALUES (12, 4, 13, '武则天', 1, 17, '2026-07-08 13:42:41');
INSERT INTO `answer_detail` VALUES (13, 4, 14, '星期六', 1, 6, '2026-07-08 13:42:41');
INSERT INTO `answer_detail` VALUES (14, 4, 15, 'A,B,C,D', 1, 0, '2026-07-08 13:42:41');
INSERT INTO `answer_detail` VALUES (15, 5, 5, 'D', 0, 3, '2026-07-08 13:48:07');
INSERT INTO `answer_detail` VALUES (16, 5, 6, 'A', 1, 22, '2026-07-08 13:48:07');
INSERT INTO `answer_detail` VALUES (17, 5, 7, '分治', 1, 0, '2026-07-08 13:48:07');

-- ----------------------------
-- Table structure for answer_sheet
-- ----------------------------
DROP TABLE IF EXISTS `answer_sheet`;
CREATE TABLE `answer_sheet`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `exam_id` bigint(20) NULL DEFAULT NULL,
  `submit_time` datetime NULL DEFAULT NULL,
  `score` int(11) NULL DEFAULT 0,
  `status` int(11) NULL DEFAULT 0,
  `spend_time` bigint(20) NULL DEFAULT NULL,
  `screen_switch_count` int(11) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `exam_id`(`exam_id`) USING BTREE,
  CONSTRAINT `answer_sheet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `answer_sheet_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of answer_sheet
-- ----------------------------
INSERT INTO `answer_sheet` VALUES (1, 3, 1, '2026-07-07 19:06:34', 20, 1, 31, 0, '2026-07-07 19:06:34', '2026-07-07 19:06:34');
INSERT INTO `answer_sheet` VALUES (2, 3, 2, '2026-07-07 20:01:03', 30, 1, 36, 0, '2026-07-07 20:01:03', '2026-07-07 20:01:03');
INSERT INTO `answer_sheet` VALUES (3, 3, 3, '2026-07-07 20:11:58', 30, 1, 22, 0, '2026-07-07 20:11:58', '2026-07-07 20:11:58');
INSERT INTO `answer_sheet` VALUES (4, 3, 4, '2026-07-08 13:42:41', 50, 1, 40, 0, '2026-07-08 13:42:41', '2026-07-08 13:42:41');
INSERT INTO `answer_sheet` VALUES (5, 4, 2, '2026-07-08 13:48:07', 20, 1, 40, 0, '2026-07-08 13:48:07', '2026-07-08 13:48:07');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `duration` int(11) NOT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT 0,
  `random_question` tinyint(1) NULL DEFAULT 0,
  `random_option` tinyint(1) NULL DEFAULT 0,
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, 'unit1', 60, '2026-07-08 12:30:00', '2026-07-08 13:30:00', 2, 0, 0, '??????', '2026-07-07 18:58:44', '2026-07-07 19:43:27');
INSERT INTO `exam` VALUES (2, 'unit2', 60, '2026-07-08 18:00:00', '2026-07-08 19:00:00', 1, 0, 0, '第二单元的考试', '2026-07-07 19:18:33', '2026-07-07 19:52:44');
INSERT INTO `exam` VALUES (3, 'unit3', 60, '2026-07-09 14:00:00', '2026-07-09 15:00:00', 1, 0, 0, '第三单元测试', '2026-07-07 19:53:29', '2026-07-07 20:00:09');
INSERT INTO `exam` VALUES (4, 'unit4', 60, '2026-07-08 15:30:00', '2026-07-08 16:30:00', 1, 0, 0, '', '2026-07-08 13:29:37', '2026-07-08 13:41:18');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `exam_id` bigint(20) NULL DEFAULT NULL,
  `type` int(11) NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `options` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `score` int(11) NULL DEFAULT 10,
  `answer` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `knowledge_point` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `analysis` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `exam_id`(`exam_id`) USING BTREE,
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 1, '测试题目', '[{\"label\":\"A\",\"value\":\"选项A\"},{\"label\":\"B\",\"value\":\"选项B\"}]', 10, 'A', '测试', '解析', '2026-07-07 18:58:44', '2026-07-07 18:58:44');
INSERT INTO `question` VALUES (2, 1, 3, '1.	软件分为应用软件、系统软件和________三类。', '[{\"label\":\"A\",\"value\":\"选项A\"},{\"label\":\"B\",\"value\":\"选项B\"}]', 10, '支撑软件', '测试', '软件分类', '2026-07-07 19:00:46', '2026-07-07 19:00:46');
INSERT INTO `question` VALUES (3, 1, 3, '2.	面向对象设计六大原则缩写为 SOLID，其中 S 代表________原则。', '[{\"label\":\"A\",\"value\":\"选项A\"},{\"label\":\"B\",\"value\":\"选项B\"}]', 10, '单一职责', '测试', '面向对象六大原则', '2026-07-07 19:01:45', '2026-07-07 19:01:45');
INSERT INTO `question` VALUES (5, 2, 1, '1.	UML 类图中，描述 “整体包含部分、部分不能脱离整体存在” 的关系是（）', '[{\"label\":\"A\",\"value\":\"关联\"},{\"label\":\"B\",\"value\":\"泛化\"},{\"label\":\"C\",\"value\":\"组合\"},{\"label\":\"D\",\"value\":\"依赖\"}]', 10, 'C', '类图中类与类之间的关系', '组合整体包含部分、部分不能脱离整体存在,聚合可以分离', '2026-07-07 19:21:18', '2026-07-07 19:21:18');
INSERT INTO `question` VALUES (6, 2, 1, '2.分布式开源协同开发依赖的核心机制是（）', '[{\"label\":\"A\",\"value\":\"Pull Request  \"},{\"label\":\"B\",\"value\":\"结对编程\"},{\"label\":\"C\",\"value\":\"正向工程\"},{\"label\":\"D\",\"value\":\"回归测试\"}]', 10, 'A', '分布式开源协同开发依赖的核心机制', '分布式开源协同开发依赖的核心机制Pull Request ', '2026-07-07 19:23:28', '2026-07-07 19:23:28');
INSERT INTO `question` VALUES (7, 2, 3, '1.软件工程八大基本原则中，将复杂问题拆分简化的思想称为________。', '[{\"label\":\"A\",\"value\":\"Pull Request  \"},{\"label\":\"B\",\"value\":\"结对编程\"},{\"label\":\"C\",\"value\":\"正向工程\"},{\"label\":\"D\",\"value\":\"回归测试\"}]', 10, '分治', '软件工程八大基本原则的核心思想', '软件工程八大基本原则中，将复杂问题拆分简化的思想称为分治', '2026-07-07 19:25:08', '2026-07-07 19:25:08');
INSERT INTO `question` VALUES (8, 3, 1, '1.以下哪种颜色最能营造紧张、压迫感的视觉氛围？', '[{\"label\":\"A\",\"value\":\"浅蓝\"},{\"label\":\"B\",\"value\":\"米白\"},{\"label\":\"C\",\"value\":\"深红\"},{\"label\":\"D\",\"value\":\"淡紫\"}]', 10, 'C', '视觉氛围', '红色系最能营造紧张、压迫感的视觉氛围', '2026-07-07 19:56:22', '2026-07-07 19:56:22');
INSERT INTO `question` VALUES (9, 3, 1, '2.以下哪种布局方式最适合突出题目核心内容？', '[{\"label\":\"A\",\"value\":\"全屏化题目区域，隐藏冗余统计卡片\"},{\"label\":\"B\",\"value\":\"顶部铺满导航栏，底部留白\"},{\"label\":\"C\",\"value\":\"左右分栏展示题目和错题本\"},{\"label\":\"D\",\"value\":\"弹窗式显示题目选项\"}]', 10, 'A', '布局方式', '全屏化题目区域，隐藏冗余统计卡片最适合突出题目核心内容', '2026-07-07 19:57:54', '2026-07-07 19:57:54');
INSERT INTO `question` VALUES (10, 3, 2, '3.下列属于软件质量要素的是()', '[{\"label\":\"A\",\"value\":\"安全性\"},{\"label\":\"B\",\"value\":\"健壮性\"},{\"label\":\"C\",\"value\":\"机械性\"},{\"label\":\"D\",\"value\":\"可移植性\"}]', 10, 'A,B,D', '软件质量要素', '略', '2026-07-07 19:59:44', '2026-07-07 19:59:44');
INSERT INTO `question` VALUES (11, 4, 1, '1.下列哪种硬件指令常用于实现互斥锁?', '[{\"label\":\"A\",\"value\":\"ADD\"},{\"label\":\"B\",\"value\":\"JMP\"},{\"label\":\"C\",\"value\":\"Test-and-Set\"},{\"label\":\"D\",\"value\":\"MOV\"}]', 10, 'C', '硬件指令常用于实现互斥锁', 'Test-and-Set指令常用于实现互斥锁', '2026-07-08 13:33:24', '2026-07-08 13:33:24');
INSERT INTO `question` VALUES (12, 4, 1, '2.在 many-to-one 线程模型中，多个用户线程映射到(        )', '[{\"label\":\"A\",\"value\":\"一个进程\"},{\"label\":\"B\",\"value\":\" 多个内核线程\"},{\"label\":\"C\",\"value\":\"多个CPU\"},{\"label\":\"D\",\"value\":\"一个内核线程\"}]', 10, 'D', 'many-to-one 线程模型', 'many-to-one 线程模型多个用户线程映射到一个内核线程', '2026-07-08 13:35:27', '2026-07-08 13:35:27');
INSERT INTO `question` VALUES (13, 4, 3, '3.我国历史上唯一的女皇帝是()', '[{\"label\":\"A\",\"value\":\"一个进程\"},{\"label\":\"B\",\"value\":\" 多个内核线程\"},{\"label\":\"C\",\"value\":\"多个CPU\"},{\"label\":\"D\",\"value\":\"一个内核线程\"}]', 10, '武则天', '中国古代史', '我国历史上唯一的女皇帝是武则天', '2026-07-08 13:37:01', '2026-07-08 13:37:01');
INSERT INTO `question` VALUES (14, 4, 3, '4.星期日的前一天是()', '[{\"label\":\"A\",\"value\":\"一个进程\"},{\"label\":\"B\",\"value\":\" 多个内核线程\"},{\"label\":\"C\",\"value\":\"多个CPU\"},{\"label\":\"D\",\"value\":\"一个内核线程\"}]', 10, '星期六', '生活常识', '略', '2026-07-08 13:38:16', '2026-07-08 13:38:16');
INSERT INTO `question` VALUES (15, 4, 2, '5.下列属于页面置换算法的是(        )', '[{\"label\":\"A\",\"value\":\"FIFO\"},{\"label\":\"B\",\"value\":\"LRU\"},{\"label\":\"C\",\"value\":\"MIN\"},{\"label\":\"D\",\"value\":\"CLOCK\"}]', 10, 'A,B,C,D', '页面置换算法', '略', '2026-07-08 13:41:07', '2026-07-08 13:41:07');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT 'STUDENT',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$eHifBG8EkUrVmRVwQTuaHeXNxjTHErwRV9.uiN1tYshc43VFoqW/W', 'ADMIN', NULL, NULL, NULL, '2026-07-07 18:54:36', '2026-07-07 18:58:28');
INSERT INTO `user` VALUES (2, 'user1', '$2a$10$rFKAxgve/JzKF9OMHeW0Sei9cn6/st7e540lm3Ep096pEm0eTktaK', 'STUDENT', NULL, '123456@qq.com', '12345122345', '2026-07-07 19:03:04', '2026-07-07 19:03:04');
INSERT INTO `user` VALUES (3, 'testuser', '$2a$10$tz17cQ2r801Jw1kELFlUR.ZLOdIXcgVVnxZ6IpxWlIaqfFrQWK2hS', 'STUDENT', NULL, '123456@qq.com', '13456879012', '2026-07-07 19:05:32', '2026-07-07 19:05:32');
INSERT INTO `user` VALUES (4, 'testuser1', '$2a$10$qjFKc.t8xoNWpCKgyH/6QetnZzexzZGnDZ5Z66BYIyeLjjuGC785W', 'STUDENT', NULL, '123678@qq.com', '13567815670', '2026-07-08 13:46:16', '2026-07-08 13:46:16');

-- ----------------------------
-- Table structure for wrong_question
-- ----------------------------
DROP TABLE IF EXISTS `wrong_question`;
CREATE TABLE `wrong_question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `question_id` bigint(20) NULL DEFAULT NULL,
  `wrong_count` int(11) NULL DEFAULT 1,
  `last_wrong_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  CONSTRAINT `wrong_question_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `wrong_question_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of wrong_question
-- ----------------------------
INSERT INTO `wrong_question` VALUES (1, 4, 5, 1, '2026-07-08 13:48:07', '2026-07-08 13:48:07', '2026-07-08 13:48:07');

SET FOREIGN_KEY_CHECKS = 1;
