ALTER TABLE `user`
  ADD COLUMN `refresh_token` text COMMENT '발급된 리프레시 토큰' AFTER `profile_image_url`;
