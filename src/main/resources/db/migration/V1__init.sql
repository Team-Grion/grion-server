SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `user` (
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '사용자 PK',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
  `nickname` varchar(50) NOT NULL COMMENT '사용자 닉네임',
  `kakao_id` varchar(255) NOT NULL COMMENT '카카오 고유 식별값',
  `profile_image_url` text COMMENT '카카오 프로필 이미지 URL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4tp32nb01jmfcirpipti37lfs` (`kakao_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pet` (
  `birth_date` date DEFAULT NULL COMMENT '반려동물 생일',
  `death_date` date DEFAULT NULL COMMENT '이별한 날짜',
  `is_shared` tinyint(1) NOT NULL DEFAULT '0' COMMENT '공개 여부',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '반려동물 PK',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
  `user_id` bigint NOT NULL,
  `breed` varchar(50) NOT NULL COMMENT '상세 품종',
  `name` varchar(50) DEFAULT NULL COMMENT '반려동물 이름',
  `background_text` varchar(255) DEFAULT NULL COMMENT '추모 페이지 배경 설명',
  `memories` text COMMENT '반려동물과의 추억 기록',
  `species` enum('CAT','DOG') NOT NULL COMMENT '반려동물 종 (CAT, DOG)',
  PRIMARY KEY (`id`),
  KEY `FK_PET_USER` (`user_id`),
  CONSTRAINT `FK_PET_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ai_image_task` (
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '작업 PK',
  `pet_id` bigint NOT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
  `status` varchar(20) NOT NULL COMMENT '작업 상태 (PENDING, SUCCESS, FAIL)',
  `failure_reason` text COMMENT '실패 사유',
  `request_id` varchar(255) NOT NULL COMMENT 'AI API 요청 ID',
  `result_url` text COMMENT 'AI 편집 결과 이미지 URL',
  PRIMARY KEY (`id`),
  KEY `FK_AI_IMAGE_TASK_PET` (`pet_id`),
  CONSTRAINT `FK_AI_IMAGE_TASK_PET` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `message` (
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '익명 여부',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '메시지 PK',
  `pet_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `status` varchar(20) NOT NULL COMMENT '메시지 상태',
  `content` text NOT NULL COMMENT '메시지 본문',
  PRIMARY KEY (`id`),
  KEY `FK_MESSAGE_PET` (`pet_id`),
  KEY `FK_MESSAGE_USER` (`sender_id`),
  CONSTRAINT `FK_MESSAGE_PET` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`),
  CONSTRAINT `FK_MESSAGE_USER` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pet_image` (
  `is_main` tinyint(1) NOT NULL DEFAULT '0' COMMENT '대표 사진 여부',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '이미지 PK',
  `pet_id` bigint NOT NULL,
  `image_url` text NOT NULL COMMENT '이미지 경로',
  PRIMARY KEY (`id`),
  KEY `FK_PET_IMAGE_PET` (`pet_id`),
  CONSTRAINT `FK_PET_IMAGE_PET` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pet_personality` (
  `pet_id` bigint NOT NULL,
  `personality` varchar(50) NOT NULL,
  PRIMARY KEY (`pet_id`, `personality`),
  CONSTRAINT `FK_PET_PERSONALITY_PET` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
