# <div align="center"> ShowFolio </div>

개발자들의 프로젝트 공유와 네트워킹을 넘어, AI 기반의 맞춤형 포트폴리오 진단 및 이력서 최적화까지 지원하는 개발자 전용 커리어 SNS

> 개발 기간 : 2026.05.22 ~ 2026.06.08

---

## <div align="center">기술 스택</div>

> ### 백엔드

- Spring Boot(4.0.6)
- JDK (>= 21)
- MySQL
- JPA, Spring Data JPA, QueryDSL
- Lombok
- Spring Security

> ### 프론트 엔드

- React

> ### API
- Gemini-2.5-flash(AI기능)
- NanumGothic폰트(PDF변환기능)

> ### 협업 툴

- Github
- Notion
- ERDCloud
- GoogleDrive

---

## <div align="center">개발 시작하기</div>

> ### 로컬 서버 접속하기

> env 플러그인 추가 : (https://popcorn-overflow.tistory.com/18) 참고

1. 플러그인 EnvFile을 IntelliJ에 설치
2. 프로젝트 root에 .env파일 생성
3. .env.example 참고해서 키-값 채우기
4. IntelliJ에서 Application 실행버튼의 드랍다운클릭 -> Edit Configurations
5. Enable EnvFile 체크박스를 체크하고 하단의 + 아이콘 클릭 -> root에 생성한 .env 파일 선택

> IntelliJ 설정 : Build, Execution, Deployment -> Compilers ->

1. Annotation Processors : Enable annotation processing 체크활성화
2. Java Compilder : Additional command line parameters에 -parameters 추가

---

## <div align="center">개발 규칙</div>

> ### 브랜치 규칙

- **main**: 배포용
- **develop**: 개발용
- **개인 브랜치**: 각자 개발하는 기능별 브랜치 생성
    - `feat/기능명` (ex: `feat/login`)
- **주의**: 개발용 develop에서 개발 진행하고 배포시 main에 머지합니다.

> ### PR 및 Merge 규칙

- 브랜치 Pull Request(PR) 전에는 반드시 팀원들에게 알리고 함께 진행합니다.

> ### 커밋 규칙

- 커밋의 끝맺음은 "~ 기능 추가", "~ 작업", "~ 개발" 과 같이 명사로 통일
- 너무 많은 변경을 하나의 커밋에 담지 말기 (세부 작업마다 틈틈이 커밋하기!)
- **형식**: `[타입]: 설명`

| 타입           | 설명                             |
|:-------------|:-------------------------------|
| **Init**     | 프로젝트 세팅                        |
| **Feat**     | 새로운 기능 추가                      |
| **Fix**      | 버그 수정                          |
| **Design**   | UI 스타일/디자인 수정                  |
| **Refactor** | 코드 리팩토링                        |
| **Typo**     | 오타 수정, 타입 수정                   |
| **Rename**   | 폴더 구조 이동, 폴더 및 파일명 변경          |
| **Del**      | 폴더, 파일 삭제                      |
| **Assets**   | 이미지, 폰트 등 리소스 추가/삭제            |
| **Deps**     | 의존 라이브러리 설치 및 삭제               |
| **Docs**     | 문서 수정 (README 등)               |
| **Style**    | 코드 의미에 영향 없는 포맷 변경 (세미콜론 누락 등) |

- **작성 예시**:
    - `Feat: 로그인 기능`
    - `Fix: 로그인 시 비밀번호 불일치 오류 수정`
    - `Refactor: 중복되는 데이터 조회 로직을 함수로 분리`

> ### 4. 폴더/파일명 규칙

- **폴더**: kebab-case + 소문자 형태 사용 (ex: `user-profile`, `controller`, `service`, `models`등)
- **클래스 및 인터페이스 파일**: PascalCase 형태 사용 (ex: `MemberController`, `MemberService`등)
- **컴포넌트 파일**: PascalCase 형태 사용 (ex: `UserProfile`, `Home`등)

> ### 5. 환경변수

- **.gitignore 설정**: `node_modules`, `.env` 같은 파일 포함
- **.env 작성**: DB 접속 정보(user, password)나 API 키 등 보안 정보 작성