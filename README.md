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
    - **user_idx**: 유저 인덱스 (BIGNINT UNSIGNED, 기본 키)
        - **선택 이유**: `BIGINT UNSIGNED`는 0 이상의 큰 숫자를 표현할 수 있으며, 유저 수가 많을 경우에도 충분한 범위를 제공합니다. 소규모 사이트라면 `INT`를 사용할 수 있지만, 확장 가능성을 고려해 `BIGINT`를 사용했습니다.
    - **email**: 이메일 (VARCHAR(350), NOT NULL)
        - 아이디 별로, 도메인 별로 글자 수 차이가 있고 최대 이메일의 길이가 300자 이상이라 `VARCHAR(350)`로 설정했습니다.
        - 다음 번에는 로그인 시 성능을 고려하고 중복을 방지하여 UNIQUE 키로 설정하고 싶습니다.
    - **password**: 비밀번호 (CHAR(60), NOT NULL)
        - **선택 이유**: bcrypt로 암호화된 비밀번호는 고정된 60자의 길이를 가지므로 `CHAR(60)`을 사용했습니다.
    - **created_at**: 가입 일시 (TIMESTAMP, NOT NULL)
    - **updated_at**: 수정 일시 (TIMESTAMP, NOT NULL)
        - **TIMESTAMP 선택 이유**: 타임존이 고려되어 시간 정보를 저장할 수 있으며, 사이트가 글로벌 확장될 경우에도 시간대별로 정렬이 가능합니다. 또한 차지하는 메모리 공간이 `DATETIME`보다 적기 때문에 `TIMESTAMP`를 선택했습니다.
     
          예시 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@222
  2 상품 테이블
  3 장바구니 테이블
  4 사이트 결제기록 테이블
  5 토스페이 결제 테이블
  




  

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
