# **E-Commerce-Payment_system**
스프링 부트를 사용해 오픈소스인 토스 결제 api랑 결합해 e-커머스 시스템을 한번 구현 해봤습니다 . 인기 웹사이트라는 가정하에 대규모 트래픽을 처리할 수 있도록 Redis 캐싱과 메시지 큐(RabbitMQ)를 활용하여 서버 부하를 최소화하며 오픈 API 연동을 통해 원하는 방식으로 서버 간 통신을 설계하며 확장성과 유연성을 고려한 구조를 직접 구현해 보았습니다


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
     
          예시 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@22
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
       - 장바구니 추가/삭제/변경하면 최근에 언제 바꿨는지 시간 데이터 저장
   
  

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




  

  ##💡  개발 스택
  </details>
  이 섹션은 기본적으로 숨겨져 있으며, "더 보기"를 클릭하면 내용을 확인할 수 있습니다.

  - 추가 기능 1
  - 추가 기능 2

</details>


### 상세 설명
이 섹션은 기본적으로 숨겨져 있으며, "더 보기"를 클릭하면 내용을 확인할 수 있습니다.

- 추가 기능 1
- 추가 기능 2

</details>
