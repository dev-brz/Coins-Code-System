INSERT INTO article_images(id, content)
VALUES  (1001, 'test-img-content');

INSERT INTO articles(id, uid, version, title, description, content, created_at, image_id)
VALUES  (1001, '1', 0, 'test-img', 'test-img', 'test-img', CURRENT_TIMESTAMP, 1001);
