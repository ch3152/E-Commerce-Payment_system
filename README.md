# **E-Commerce-Payment_system (Spring Boot & 토스페이 연동)**
스프링 부트를 사용해 오픈소스인 토스 결제 api랑 결합해 e-커머스 시스템을 한번 구현 해봤습니다 . 인기 웹사이트라는 가정하에 대규모 트래픽을 처리할 수 있도록 Redis 캐싱과 메시지 큐(RabbitMQ)를 활용하여 서버 부하를 최소화하며 오픈 API 연동을 통해 원하는 방식으로 서버 간 통신을 설계하며 확장성과 유연성을 고려한 구조를 직접 구현하고 마지막으로 구글 클라우드 서비스를 이용한 배포까지 한번 해보았습니다.


-    ⬇️ **아래를 눌러 실제 구현 사진을 확인하세요!**
- <details>
    <summary>🔽 [더보기 클릭!] 👉(이커머스 페이지 실제 구현 사진)</summary>
  
    ### 로그인
    ![image](https://github.com/user-attachments/assets/a467ca84-46e4-4c6e-ae18-27c39f2cb73e)
  
    ### 회원가입
    ![image](https://github.com/user-attachments/assets/56b747bc-247c-4db8-89fe-e84fe3dc92b6)

    ### 장바구니
    ![image](https://github.com/user-attachments/assets/c6ed9818-f260-43eb-9099-88a4fd1bd243)

    ### 결제하기 클릭 후
    ![image](https://github.com/user-attachments/assets/d019ed13-d96c-40dc-a15a-f01baa8bca89)

    ![image](https://github.com/user-attachments/assets/52c035f5-c148-4c91-b38f-bd7a1be0f07e)
  
    ###  결제 완료 후 json형식으로 추가 정보 출력
    ![image](https://github.com/user-attachments/assets/8b41c4fb-8a91-4f9d-9493-c9c3bc979c90)

    ### 그 외 db저장 사진
    ![image](https://github.com/user-attachments/assets/546130ab-dcff-421d-956c-d597575aae44)
  </details>




  



  

    









## 제작 기간
2024 10월~2025 1월

## 전체적인 아키텍처
<img src="https://github.com/user-attachments/assets/1840ed79-e880-4788-b883-eeeafedcec43" width="600" />




## 테이블 구성
<details>
  <summary>테이블 구성 정보(ERD 설계도 및 테이블 간 관계/컬럼 설명)</summary>

  ![image](https://github.com/user-attachments/assets/9f543a9e-347b-4fd8-bce3-f3170f30be48)

  1 유저 테이블


  ![image](https://github.com/user-attachments/assets/a58aa0ec-3044-4c76-bd76-c13eacb1ec17)

  유저의 기본 정보를 저장하는 테이블입니다.

    - **user_id**: 유저 번호 (BIGNINT UNSIGNED, 기본 키)
        -  `INT`를 사용할 수 있지만,유저 수가 많은 대규모 사이트라는 생각을 구현 함으로써 확장 가능성을 고려해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 가입순서대로 번호 매기게 함
    - **name**: 유저 이름 (VARCHAR(255), NOT NULL)
    - **phone**: 유저 핸드폰 (VARCHAR(11), NOT NULL)
        - 핸드폰 번호 길이에 맞게 11자로 제한 함
    - **signup_id**: 유저 아이디 (VARCHAR(15), NOT NULL)
        - 아이디 길이를 15자로 제한, 중복 방지로 'JPA'에서 @Column(unique = true) 설정과 existsBySignupId(signupId) 메서드를 사용하여 회원가입 시 중복된 이메일이 저장되지 않도록 처리함.
    - **signup_password**:유저 비밀번호 (varchar(255), NOT NULL)
        - bcrypt라는 비밀번호 해싱 알고리즘을 사용함으로써 암호화된 비밀번호로 db에 저장되게 힘을 가함
    - **created_at**: 생성 날짜 (TIMESTAMP, NOT NULL)
    - **updated_at**: 업데이트 날짜 (datetime(6), NOT NULL)
        - 회원가입 정보 수정하면 최근에 언제 바꿨는지 시간 데이터 저장
     
     
  2 상품 테이블
  ![image](https://github.com/user-attachments/assets/a59509bb-d20f-4087-8267-672465a6b450)

  웹 사이트에 있는 모든 상품 정보들을 담고있는 테이블 입니다.

    - **prodcut_id**: 상품 번호 (BIGNINT UNSIGNED, 기본 키)
        - 상품 번호도`INT`를 사용할 수 있지만,수많은 상품 등록을 예상해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 상품 등록 순서대로 번호 매기게함.
    - **name**: 상품 이름 (VARCHAR(255), NOT NULL)
    - **price**: 상품 가격(INT, NOT NULL)
    - **category**: 상품 종류 (VARCHAR(255), NOT NULL)
    - **country**: 상품 이름 (VARCHAR(255), NOT NULL)
    - **manufacturer**: 제조 업체 (VARCHAR(255), NOT NULL)
    - **quantity**: 재고 수량 (INT, NOT NULL)
      


  3 장바구니 테이블
  ![image](https://github.com/user-attachments/assets/4de8cfa4-b2f2-4280-bb57-06b00b80ad18)

  유저들의 장바구니 데이터를 담고 있는 테이블입니다
  


   - **cart_id**: 유저 번호 (BIGNINT UNSIGNED, 기본 키)
        - 수많은 장바구니 데이터 등록을 예상해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 장바구니 생성 순서대로 번호 매기게함.
   - **prodcut_id**: 상품 이름 (BIGNINT UNSIGNED, 유니크 키)
        - 상품 테이블의 상품 ID를 참조하는 외래 키로, 장바구니에 상품이 추가되면 해당 상품의 번호가 저장됨 이후 상품 번호를 활용하여 상품 테이블에서 가격 및 재고 수량을 조회하여 표시함.
   - **user_id**: 상품 이름 (BIGNINT UNSIGNED, 유니크 키)
        - 유저 테이블의 ID를 참조하는 외래 키로, 장바구니가 특정 사용자에게 속하도록 설정됨 로그인한 사용자의 user_id를 기반으로 장바구니 목록을 조회하여 해당 사용자의 장바구니 정보를 가져올 수 있음.
   - **signup_id**: 유저 아이디 (VARCHAR(15), NOT NULL)
   - **quantity**: 재고 수량 (INT, NOT NULL)
       - 사용자가 장바구니에 추가한 상품의 개수를 저장하는 필드 동일한 상품을 여러 개 담을 경우, 해당 상품의 quantity 값이 증가함 또한 추가하기전 상품 id를 통해 재고가 남아있는지 확인
   - **created_at**: 생성 날짜 (TIMESTAMP, NOT NULL)
   - **updated_at**: 업데이트 날짜 (datetime(6), NOT NULL)
       - 장바구니 추가/삭제/변경하면 최근에 언제 바꿨는지 시간 데이터 저장.
   
  

  4 사이트 결제기록 테이블
  ![image](https://github.com/user-attachments/assets/fba95565-867d-4a2b-b24c-7d5218f3c819)

  사이트에서 유저가 언제, 어떤 상품을 얼마나 구매했는지 또는 결제가 성공했는지 실패했는지를 기록하는 DB 테이블입니다.
   - **web_payment_id**:결제 번호 (BIGNINT UNSIGNED, 기본 키)
        - 수많은 결제를 예상해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 결제 순서대로 번호 매기게함.
   - **user_id**: 유저 번호 (BIGNINT UNSIGNED, 유니크 키)
        - 유저 테이블의 ID를 참조하는 외래 키로, 장바구니가 특정 사용자에게 속하도록 설정됨 로그인한 사용자의 user_id를 기반으로 장바구니 목록을 조회하여 해당 사용자의 장바구니 정보를 가져올 수 있음.
   - **signup_id**: 유저 아이디 (VARCHAR(15), NOT NULL)
   - **total_quantity**: 총 수량 (INT, NOT NULL)
       - 결제하려고 하는 상품들의 총 수량을 나타냄
   - **total_price**: 총 가격 (INT, NOT NULL)
       - 결제하려고 하는 상품들의 수량과 가격들을 곱해서 총 가격을 나타냄
   - **customer_name  **: 유저 이름 (VARCHAR(255), NOT NULL)
       - user_id를 기반으로 유저 테이블에서 가져온 이름을 저장하여, 결제 내역에서 결제자의 이름을 명확히 식별할 수 있도록 함.
   - **customer_phone **: 유저 아이디 (VARCHAR(255), NOT NULL)
       - user_id를 기반으로 유저 테이블에서 가져온 이름을 저장하여, 결제 내역에서 결제자의 전화번호를 명확히 식별할 수 있도록 함.
   - **payment_status**: 결제 상태 (VARCHAR(255), NOT NULL)
       - **초기값은 "보류"이며, 결제 진행 상황에 따라 "결제 성공" 또는 "결제 실패"로 변경됨. 서버에서 결제 시스템과 연동하여 해당 값을 업데이트함
   - **created_at**: 생성 날짜 (TIMESTAMP, NOT NULL)


  5 토스페이 결제 테이블
  ![image](https://github.com/user-attachments/assets/316c3f78-12fd-410b-a70c-48e0d1055e86)

  토스페이 결제 API를 통해 처리된 결제 내역을 저장하는 테이블입니다. 해당 테이블을 통해 특정 사용자의 결제 상태를 확인합니다.

   - **toss_payment_id**:토스 결제 번호 (BIGNINT UNSIGNED, 기본 키)
        - 수많은 결제를 예상해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 결제 순서대로 번호 매기게함.
   - **payment_method **: 결제 방법 (VARCHAR(255), NOT NULL)
        - 무통장 입금,카드 결제,토스 페이 결제 등.
   - **currency**: 통화 (varchar(255) , NOT NULL)
   - **total_price**: 총 가격 (INT, NOT NULL)
       - 서버에서 결제하려고 하는 상품들의 총 가격 정보를 받아와 저장
   - **total_amount **: 총 금액 (INT, NOT NULL)
       - 토스 자체에 할인 된 금액 포함해서 총 금액이 저장 됨
   - **customer_name **: 유저 이름 (VARCHAR(255), NOT NULL)
       -  user_id를 이용해 User 테이블에서 유저의 이름을 가져와 결제 정보 테이블에 직접 저장
   - **customer_phone **: 유저 핸드폰 (VARCHAR(255), NOT NULL)
       - user_id를 이용해 User 테이블에서 유저의 전화번호를 가져와 결제 정보 테이블에 직접 저장
   - **discount**: 할인 금액 (INT, NOT NULL)
       - 토스 페이에서 할인 된 금액을 나타냄
   - **payment_time**: 생성 날짜 (TIMESTAMP, NOT NULL)  




  

  
  </details>




##  개발 스택  
`Java 17` `Spring Boot` `MySQL` `Redis` `RabbitMQ` `HTML` `JavaScript` `GCP`

## 핵심 기능
### 1 회원가입 & 로그인

1 Redis 기반 세션 관리로 빠른 인증 처리 및 로그인 속도 향상.

2 보안을 위해 bcrypt 알고리즘으로 비밀번호를 암호화하여 안전하게 저장.

### 2 장바구니
1 상품 추가, 수정, 삭제 기능
사용자가 장바구니에 상품을 추가하면 Redis에 먼저 저장.
동일한 상품이 이미 존재하면 수량 증가 방식으로 처리.
상품 삭제 및 수량 변경 시 Redis에서 즉시 반영.

2 Redis 기반 메모리 캐싱
장바구니 데이터는 DB가 아닌 Redis에서 우선 조회.
Redis에 데이터가 없으면 DB에서 조회 후 Redis에 저장하여 캐싱.
이를 통해 트래픽을 분산하고 DB 부하 감소.

3 메시지 큐(RabbitMQ) 활용한 비동기 저장
장바구니 데이터 변경 시 RabbitMQ로 메시지 발행.
백그라운드 작업이 메시지를 소비하여 비동기적으로 DB 저장.
클라이언트 요청 시 Redis에서 빠르게 응답하고, DB 저장은 비동기로 처리하여 성능 향상.

### 3 결제
1 결제 완료 시 장바구니 목록 삭제 & 상품 테이블 재고 감소
결제 성공 시 장바구니 삭제.
결제한 상품들의 재고 감소.
Redis 캐시에서 장바구니 데이터 제거.
DB에서 상품 재고 반영.

2 Toss API 연동으로 결제 및 영수증 기능 추가 (통화, 결제 내역 표시 가능)
결제 정보 JSON을 Toss API로 전송.
응답 데이터에서 결제 수단 및 통화 정보 저장.
주문 ID 기반으로 결제 내역 조회 및 영수증 제공.

3 결제 성공/실패 상태를 받아와 DB에 저장 (비동기 처리)
Toss API로부터 결제 상태(success/fail) 수신.
성공 시 TossPaymentInfo 테이블에 결제 내역 저장.
실패 시 에러 로그 기록 및 사용자에게 실패 응답 반환.
@Async 비동기 처리로 응답 속도 개선.

"토스 API 결제 프로세스 흐름"
![image](https://github.com/user-attachments/assets/bdaa700f-277b-40dc-b4dd-0623d85faba2)

## 리팩토링

#### *이전 코드
    public boolean authenticateUser(String signupId, String password) {
        String cachedSignupId = redisTemplate.opsForValue().get("login:signupId:" + signupId);
        if (cachedSignupId != null) {
            // Redis에서 로그인 처리
       } else {
           User user = userRepository.findBySignupId(signupId).orElse(null);
           // DB에서 사용자 조회
       }
      }
#### *현재 코드 

    public boolean authenticateUser(String signupId, String password) {
        User user = getUserBySignupId(signupId);}
        // getUserBySignupId 내부에서 Redis 조회 후 없으면 DB 조회
   

중복 메소드가 발견되어 코드 간소화로 똑같은 redis조회 메소드인 **User user = getUserBySignupId(signupId);** 로 대체하였다

## 트러블슈팅 경험

1 BCrypt 의존성 문제

처음에 비밀번호 암호화 알고리즘인**BCrypt**을 사용하려고 **org.springframework.boot:spring-boot-starter-security** 의존성을 추가했는데, 실행은 되지만 내가 구현한 창 대신 예상치 못한 웹사이트가 뜨는 문제가 발생했다. 찾아보니 **spring-boot-starter-security** 의존성은 Spring Security의 전체 보안 기능을 포함하는 스타터라 비밀번호 암호화만 사용하려면 **spring-security-crypto** 의존성을 추가해야 한다는 것을 알게 되었다.

2 Redis 직렬화 문제

Redis에 데이터를 저장할 때, **RedisTemplate<String, String>**을 사용하여 데이터를 변환하면서 직렬화와 역직렬화 문제가 발생했다. 직렬화 방식에 대한 이해가 부족해 처음에는 데이터가 제대로 저장되지 않거나,다른 곳 서로 간 통신에서 문제가 발생하기해 한참을 헤맨 뒤  **Jackson2JsonRedisSerializer**를 사용해 직렬화 방식을 JSON으로 지정했더니 그 이후로 통신이 원할히 이루워졌다.

## 회고록
첫 개인 프로젝트를 만들면서 자바스프링, redis, 메세지큐, 오픈api 등 처음 써봤는데 생각보다 어려웠습니다, 특히 자바스프링의 의존성 개념이 처음에 뭔지 몰라 실행이 중간에 꺼져버려 장시간 동안 헤맨 기억이 있었고 프로젝트라는게 생각보다 원하는대로 되는게 아니라 수정하고 또 수정하느라 예상 완료 시간이 많이 지체되면서 처음 설계한 것 보다 완벽하게 구현을 못한게 아쉽습니다. 다음 프로젝트에는 유저의 검색 기록 및 쿠키 데이터를 분석하여 개인화된 상품을 추천하는 ai추천 기능을 한번 구현 해보고 싶습니다.
