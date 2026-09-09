ALTER TABLE `pet`
  ADD COLUMN `memory` text DEFAULT NULL COMMENT '함께한 추억 (비공개, 화면 미노출)' AFTER `memories`;

ALTER TABLE `pet`
  MODIFY COLUMN `memories` text COMMENT '한줄 소개 (isPublic과 함께 노출)';
