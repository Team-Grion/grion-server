ALTER TABLE `pet`
  ADD COLUMN `deleted_at` datetime DEFAULT NULL COMMENT '삭제 일시' AFTER `updated_at`;

ALTER TABLE `message`
  ADD COLUMN `deleted_at` datetime DEFAULT NULL COMMENT '삭제 일시' AFTER `status`;
