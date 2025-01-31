 **Bold text**
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5

https://dbdiagram.io/d

# **프로젝트 이름**

## 테이블 구성
<details>
  <summary>테이블 구성 정보(ERD 설계도 및 테이블 간 관계/컬럼 설명)</summary>

  ![FULLDB](https://github.com/user-attachments/assets/f9f04b9a-3b78-4e8e-819b-ef710d4ceb14)
  1 유저 테이블
  
  예시 @@@@@@@@@@@@@@@@@@@@@@@@@@@
  유저의 기본 정보를 저장하는 테이블입니다.

- 컬럼 설명


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
  Table: web_payment_info
Columns:
id bigint AI PK 
user_id bigint 
total_price int 
total_quantity bigint 
customer_name varchar(255) 
customer_phone varchar(255) 
payment_status varchar(255) 
created_at timestamp
   - **web_payment_id**:결제 번호 (BIGNINT UNSIGNED, 기본 키)
        - 수많은 장바구니 데이터 등록을 예상해 `BIGINT`를 사용함,@GeneratedValue(strategy = GenerationType.IDENTITY)설정으로 결제 순서대로 번호 매기게함.
   - **user_id**: 상품 이름 (BIGNINT UNSIGNED, 유니크 키)
        - 유저 테이블의 ID를 참조하는 외래 키로, 장바구니가 특정 사용자에게 속하도록 설정됨 로그인한 사용자의 user_id를 기반으로 장바구니 목록을 조회하여 해당 사용자의 장바구니 정보를 가져올 수 있음.
   - **signup_id**: 유저 아이디 (VARCHAR(15), NOT NULL)
   - **total_quantity**: 총 수량 (INT, NOT NULL)
       - 결제하려고 하는 상품들의 총 수량을 나타냄
   - **total_price**: 상품 이름 (INT, NOT NULL)
       - 결제하려고 하는 상품들의 수량과 가격들을 곱해서 총 가격을 나타냄
   - **customer_name  **: 유저 아이디 (VARCHAR(255), NOT NULL)
       - 유저 정보 바탕으로 결제하는 손님 이름 저장 
   - **customer_phone **: 유저 아이디 (VARCHAR(255), NOT NULL)
   - **payment_status**: 총 수량 (VARCHAR(255), NOT NULL)
       - 사용자가 장바구니에 추가한 상품의 개수를 저장하는 필드 동일한 상품을 여러 개 담을 경우, 해당 상품의 quantity 값이 증가함 또한 추가하기전 상품 id를 통해 재고가 남아있는지 확인
    
    
   - 
   - **created_at**: 생성 날짜 (TIMESTAMP, NOT NULL)
   - **updated_at**: 업데이트 날짜 (datetime(6), NOT NULL)
       - 장바구니 추가/삭제/변경하면 최근에 언제 바꿨는지 시간 데이터 저장


  5 토스페이 결제 테이블
  Table: toss_payment_info
Columns:
id bigint AI PK 
payment_method varchar(255) 
currency varchar(255) 
country varchar(255) 
total_amount int 
customer_name varchar(255) 
customer_phone varchar(255) 
payment_time timestamp 
discount varchar(255)
  




  

  ### 상세 설명
  이 섹션은 기본적으로 숨겨져 있으며, "더 보기"를 클릭하면 내용을 확인할 수 있습니다.

  - 추가 기능 1
  - 추가 기능 2

</details>


### 상세 설명
이 섹션은 기본적으로 숨겨져 있으며, "더 보기"를 클릭하면 내용을 확인할 수 있습니다.

- 추가 기능 1
- 추가 기능 2

</details>
