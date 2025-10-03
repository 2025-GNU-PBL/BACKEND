-- OWNER 데이터 삽입
INSERT INTO owner (id,
                   profile_image,
                   bz_name,
                   age,
                   phone_number,
                   bz_number,
                   bank_account,
                   user_role,
                   email,
                   name,
                   social_id,
                   social_provider,
                   created_at,
                   updated_at)
VALUES (5, 'https://cdn.example.com/owner1.jpg', '청담 뷰티샵', 35, '010-1234-5678', '111-22-33333',
        '110-123-456789', 'OWNER',
        'owner1@example.com', '홍길동', 'social-123', 'KAKAO', NOW(), NOW()),

       (6, 'https://cdn.example.com/owner2.jpg', '서초 메이크업 스튜디오', 32, '010-2345-6789',
        '222-33-44444', '110-987-654321', 'OWNER',
        'owner2@example.com', '김서연', 'social-456', 'GOOGLE', NOW(), NOW()),

       (7, 'https://cdn.example.com/owner3.jpg', '송파 뷰티살롱', 29, '010-3456-7890', '333-44-55555',
        '110-555-666777', 'OWNER',
        'owner3@example.com', '박지훈', 'social-789', 'NAVER', NOW(), NOW()),

       (8, 'https://cdn.example.com/owner4.jpg', '해운대 스타일', 40, '010-4567-8901', '444-55-66666',
        '110-111-222333', 'OWNER',
        'owner4@example.com', '정유진', 'social-101', 'KAKAO', NOW(), NOW()),

       (9, 'https://cdn.example.com/owner5.jpg', '수영구 메이크업', 37, '010-5678-9012', '555-66-77777',
        '110-888-999000', 'OWNER',
        'owner5@example.com', '최현우', 'social-202', 'GOOGLE', NOW(), NOW());


INSERT INTO product (id, owner_id, product_type, category, price, address, detail, star_count,
                     average_rating, name, created_at, updated_at)
VALUES (1, 5, 'MAKEUP', 'MAKEUP', 150000, '서울 강남구', '자연스러운 웨딩 메이크업', 4.5, 45,
        'Romantic Wedding Makeup', NOW(), NOW()),
       (2, 5, 'MAKEUP', 'MAKEUP', 180000, '서울 서초구', '고전적인 느낌의 신부 메이크업', 4.8, 48,
        'Classic Bridal Makeup', NOW(), NOW()),
       (3, 6, 'MAKEUP', 'MAKEUP', 130000, '서울 송파구', '트렌디한 스타일', 4.2, 42, 'Modern Chic Makeup',
        NOW(), NOW()),
       (4, 6, 'MAKEUP', 'MAKEUP', 200000, '서울 종로구', '럭셔리한 스튜디오 전용', 4.9, 49, 'Luxury Studio Makeup',
        NOW(), NOW()),
       (5, 7, 'MAKEUP', 'MAKEUP', 80000, '부산 해운대구', '데일리 내추럴 메이크업', 4.1, 41, 'Natural Daily Makeup',
        NOW(), NOW()),
       (6, 7, 'MAKEUP', 'MAKEUP', 120000, '부산 수영구', '화려한 파티 전용', 4.6, 46, 'Glam Party Makeup',
        NOW(), NOW()),
       (7, 8, 'MAKEUP', 'MAKEUP', 110000, '서울 마포구', '부드럽고 은은한 파스텔톤', 4.3, 43, 'Soft Pastel Makeup',
        NOW(), NOW()),
       (8, 8, 'MAKEUP', 'MAKEUP', 90000, '서울 영등포구', '직장인 전용 메이크업', 4.4, 44, 'Elegant Office Makeup',
        NOW(), NOW()),
       (9, 9, 'MAKEUP', 'MAKEUP', 160000, '서울 용산구', '드라마 스타일 재현', 4.7, 47, 'K-Drama Makeup', NOW(),
        NOW()),
       (10, 9, 'MAKEUP', 'MAKEUP', 140000, '서울 종로구', '한복 전용 메이크업', 4.5, 45,
        'Traditional Korean Makeup', NOW(), NOW());



INSERT INTO makeup (id, style, type, available_times)
VALUES (1, 'romantic', 'wedding', '09:00, 11:00, 14:00'),
       (2, 'classic', 'wedding', '10:00, 13:00, 16:00'),
       (3, 'modern', 'daily', '09:30, 12:00, 15:00'),
       (4, 'luxury', 'studio', '11:00, 14:00, 17:00'),
       (5, 'natural', 'daily', '08:30, 11:30, 14:30'),
       (6, 'glam', 'party', '12:00, 15:00, 18:00'),
       (7, 'soft', 'daily', '09:00, 13:00, 16:00'),
       (8, 'elegant', 'office', '08:00, 10:30, 14:00'),
       (9, 'kdrama', 'studio', '11:00, 15:00, 19:00'),
       (10, 'traditional', 'wedding', '10:00, 13:30, 17:00');

INSERT INTO image (id, product_id, url, s3key, display_order, created_at, updated_at)
VALUES (1, 1, 'https://cdn.example.com/makeup/1_thumb.jpg', 'makeup/1_thumb.jpg', 0, NOW(), NOW()),
       (2, 2, 'https://cdn.example.com/makeup/2_thumb.jpg', 'makeup/2_thumb.jpg', 0, NOW(), NOW()),
       (3, 3, 'https://cdn.example.com/makeup/3_thumb.jpg', 'makeup/3_thumb.jpg', 0, NOW(), NOW()),
       (4, 4, 'https://cdn.example.com/makeup/4_thumb.jpg', 'makeup/4_thumb.jpg', 0, NOW(), NOW()),
       (5, 5, 'https://cdn.example.com/makeup/5_thumb.jpg', 'makeup/5_thumb.jpg', 0, NOW(), NOW()),
       (6, 6, 'https://cdn.example.com/makeup/6_thumb.jpg', 'makeup/6_thumb.jpg', 0, NOW(), NOW()),
       (7, 7, 'https://cdn.example.com/makeup/7_thumb.jpg', 'makeup/7_thumb.jpg', 0, NOW(), NOW()),
       (8, 8, 'https://cdn.example.com/makeup/8_thumb.jpg', 'makeup/8_thumb.jpg', 0, NOW(), NOW()),
       (9, 9, 'https://cdn.example.com/makeup/9_thumb.jpg', 'makeup/9_thumb.jpg', 0, NOW(), NOW()),
       (10, 10, 'https://cdn.example.com/makeup/10_thumb.jpg', 'makeup/10_thumb.jpg', 0, NOW(),
        NOW()),
       (11, 1, 'https://cdn.example.com/makeup/11_thumb.jpg', 'makeup/11_thumb.jpg', 1, NOW(),
        NOW()),
       (12, 2, 'https://cdn.example.com/makeup/12_thumb.jpg', 'makeup/12_thumb.jpg', 1, NOW(),
        NOW()),
       (13, 3, 'https://cdn.example.com/makeup/13_thumb.jpg', 'makeup/13_thumb.jpg', 1, NOW(),
        NOW()),
       (14, 4, 'https://cdn.example.com/makeup/14_thumb.jpg', 'makeup/14_thumb.jpg', 1, NOW(),
        NOW()),
       (15, 5, 'https://cdn.example.com/makeup/15_thumb.jpg', 'makeup/15_thumb.jpg', 1, NOW(),
        NOW()),
       (16, 6, 'https://cdn.example.com/makeup/16_thumb.jpg', 'makeup/16_thumb.jpg', 1, NOW(),
        NOW()),
       (17, 7, 'https://cdn.example.com/makeup/17_thumb.jpg', 'makeup/17_thumb.jpg', 1, NOW(),
        NOW()),
       (18, 8, 'https://cdn.example.com/makeup/18_thumb.jpg', 'makeup/18_thumb.jpg', 1, NOW(),
        NOW()),
       (19, 9, 'https://cdn.example.com/makeup/19_thumb.jpg', 'makeup/19_thumb.jpg', 1, NOW(),
        NOW()),
       (20, 10, 'https://cdn.example.com/makeup/20_thumb.jpg', 'makeup/20_thumb.jpg', 1, NOW(),
        NOW());


-- OPTION 데이터 삽입 (옵션 가격)
INSERT INTO option (id, product_id, detail, price, name, created_at, updated_at)
VALUES (1, 1, '웨딩 본식 메이크업', 200000, '본식', NOW(), NOW()),
       (2, 1, '웨딩 리허설 메이크업', 150000, '리허설', NOW(), NOW()),
       (3, 2, '데일리 메이크업 (주간)', 80000, '주간', NOW(), NOW()),
       (4, 2, '데일리 메이크업 (야간)', 100000, '야간', NOW(), NOW());
