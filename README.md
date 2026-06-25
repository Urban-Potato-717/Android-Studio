# BookLog - 도서 리뷰 앱 데모

> 모바일 프로그래밍 기말 프로젝트  
> Android · Java · SQLite · Kakao Book Search API

BookLog는 사용자가 책을 검색하고, 책 상세 화면에서 별점과 한줄평을 남기며, 다른 리뷰와 평균 별점을 함께 확인하는 **도서 리뷰 앱 데모 버전**입니다. 서버 없이 앱 내부 SQLite 데이터베이스로 동작하고, 카카오 책 검색 API를 연결하면 외부 도서 검색과 표지 이미지 보강까지 가능합니다.

이 README는 앱의 기능, 화면 흐름, 파일 구조, 데이터 흐름을 설명하는 문서입니다.

---

## 1. 현재 데모에서 구현된 기능

| 기능 | 구현 내용 |
|---|---|
| 로그인/회원가입 | 로컬 SQLite 계정 기반 로그인, `SharedPreferences` 세션 유지 |
| 홈 화면 | 인기 도서 목록 표시, 검색어 입력 시 로컬 도서 실시간 필터링 |
| 카카오 책 검색 | 키워드로 카카오 책 검색 API 호출, 외부 도서 결과 표시 |
| 책 상세 | 책 정보, 표지, 평균 별점, 리뷰 수, 리뷰 목록 표시 |
| 리뷰 정렬 | 최신순/별점순을 `AlertDialog` 단일 선택으로 변경 |
| 리뷰 작성 | 별점, 한줄평, 스포일러 여부를 입력해 저장 |
| 평균 별점 집계 | 기본 커뮤니티 점수와 실제 리뷰 점수를 합산해 평균 계산 |
| 스포일러 블라인드 | 스포일러 리뷰는 가려서 보여주고, 확인 다이얼로그 후 공개 |
| 내 리뷰 | 로그인한 사용자가 작성한 리뷰만 모아서 표시 |
| 마이페이지 | 닉네임, 아이디, 내 리뷰 수, 로그아웃 제공 |
| 표지 이미지 | 로컬 drawable 또는 카카오 썸네일 URL을 `ImageView`에 표시 |

---

## 2. 앱 화면 흐름

```text
LoginActivity
  ├─ 세션이 있으면 HomeActivity로 바로 이동
  └─ SignupActivity로 회원가입 가능

하단 탭 화면
  ├─ HomeActivity      홈 / 인기 도서 / 로컬 검색
  ├─ MyReviewActivity  내가 작성한 리뷰 목록
  └─ MyPageActivity    프로필 / 리뷰 수 / 로그아웃

탭 위에서 열리는 화면
  ├─ SearchActivity       카카오 책 검색 결과
  ├─ BookDetailActivity   책 상세 / 평균 별점 / 리뷰 목록 / 정렬 / 스포일러
  └─ WriteReviewActivity  리뷰 작성 / 별점 / 한줄평 / 스포일러 체크
```

하단 탭은 `include_bottom_nav.xml`을 각 화면에서 include하고, `BottomNav.setup()`으로 현재 탭 색상과 클릭 이동을 처리합니다. 탭 이동 시 기존 탭 Activity를 `finish()`해서 뒤로가기 스택이 복잡해지지 않게 구성했습니다.

---

## 3. 주요 기능 상세

### 3.1 로그인

앱을 실행하면 `LoginActivity`가 먼저 열립니다. 데모 계정은 아래와 같습니다.

| 아이디 | 비밀번호 | 비고 |
|---|---|---|
| `demo` | `demo` | 닉네임 `김준영`, 내 리뷰 2건 보유 |

회원가입을 하면 새 사용자가 `users` 테이블에 저장되고, 비밀번호는 평문이 아니라 SHA-256 해시로 저장됩니다.

### 3.2 홈에서 책 보기

`HomeActivity`는 SQLite에 들어 있는 책 목록을 인기순으로 보여줍니다. 검색창에 글자를 입력하면 앱 내부 DB의 책 제목을 기준으로 즉시 필터링합니다.

검색 키보드에서 검색 실행을 누르면 `SearchActivity`로 이동하고, 이때는 로컬 DB가 아니라 카카오 책 검색 API를 사용합니다.

### 3.3 카카오 책 검색

`SearchActivity`는 사용자가 입력한 키워드로 카카오 책 검색 API를 호출합니다.

- API 키가 있으면 카카오에서 책 제목, 저자, 출판사, 출판연도, 썸네일, 소개 문구를 받아옵니다.
- 검색 결과를 누르면 해당 책을 SQLite `books` 테이블에 저장하거나, 이미 같은 제목+저자가 있으면 기존 책을 재사용합니다.
- 이후 바로 `BookDetailActivity`로 이동해 리뷰를 작성할 수 있습니다.
- API 키가 없으면 검색 화면에 키 미설정 안내를 보여주고, 나머지 앱 기능은 정상 동작합니다.

홈 화면에서도 카카오 API 키가 있으면 시드 책들의 표지/출판사/출판연도를 백그라운드에서 한 번 보강합니다. 초기 DB의 표지는 `cover_placeholder`이지만, 키가 설정된 환경에서는 카카오 썸네일 URL로 갱신될 수 있습니다.

### 3.4 책 상세와 리뷰

`BookDetailActivity`는 책 정보와 리뷰 목록을 보여주는 핵심 화면입니다.

- 책 표지, 제목, 저자, 출판사, 연도, 장르, 페이지 수 표시
- 평균 별점과 리뷰 수 표시
- 리뷰 목록 표시
- `정렬` 텍스트를 누르면 `AlertDialog`가 열리고 최신순/별점순 선택
- 오른쪽 아래 작성 버튼으로 `WriteReviewActivity` 이동

스포일러 리뷰는 `ReviewAdapter`에서 블라인드 형태로 가립니다. 사용자가 탭하면 경고 다이얼로그가 뜨고, 확인하면 해당 리뷰 내용이 보이도록 런타임 플래그를 변경합니다.

### 3.5 리뷰 작성

`WriteReviewActivity`에서 사용자는 아래 정보를 입력합니다.

- 별점: `RatingBar`
- 한줄평: `EditText`
- 스포일러 여부: `CheckBox`

저장하면 `reviews` 테이블에 들어가고, 책 상세 화면으로 돌아왔을 때 평균 별점과 리뷰 수가 다시 계산되어 반영됩니다.

---

## 4. 평균 별점 계산 방식

이 앱은 데모가 비어 보이지 않도록 각 책에 기본 커뮤니티 점수 값을 넣어두었습니다.

`books` 테이블에는 아래 두 컬럼이 있습니다.

| 컬럼 | 의미 |
|---|---|
| `base_count` | 기존 커뮤니티 리뷰 수 |
| `base_sum` | 기존 커뮤니티 별점 합 |

화면에 표시되는 값은 다음 방식으로 계산합니다.

```text
리뷰 수 = base_count + 실제 reviews 테이블의 리뷰 수
평균 별점 = (base_sum + 실제 리뷰 별점 합) / 리뷰 수
```

즉 사용자가 리뷰를 새로 작성하면 평균 별점이 실제로 변합니다. 이 계산은 `DBHelper`의 `BOOK_AGG_SELECT` 쿼리에서 처리합니다.

---

## 5. SQLite 데이터 구조

DB 파일명은 `booklog.db`이고, `DBHelper`가 생성과 쿼리를 모두 담당합니다.

| 테이블 | 주요 컬럼 | 역할 |
|---|---|---|
| `users` | `user_id`, `username`, `pw_hash`, `nickname` | 회원 정보 |
| `books` | `book_id`, `title`, `author`, `publisher`, `pub_year`, `genre`, `page_count`, `cover`, `tagline`, `base_count`, `base_sum` | 책 정보와 기본 집계값 |
| `reviews` | `review_id`, `book_id`, `user_id`, `nickname`, `rating`, `content`, `is_spoiler`, `helpful_count`, `created_at` | 사용자가 작성한 리뷰 |

시드 데이터에는 데모 계정, 인기 도서 5권, 커뮤니티 리뷰, 데모 사용자의 리뷰가 포함되어 있습니다.

---

## 6. 파일 구조

패키지 루트는 `com.example.finalproject`입니다.

```text
app/src/main/java/com/example/finalproject
├─ LoginActivity.java        로그인 화면
├─ SignupActivity.java       회원가입 화면
├─ HomeActivity.java         홈 / 인기 도서 / 로컬 검색 / 시드 표지 보강
├─ SearchActivity.java       카카오 책 검색 결과 화면
├─ BookDetailActivity.java   책 상세 / 리뷰 목록 / 정렬 / 작성 진입
├─ WriteReviewActivity.java  리뷰 작성 화면
├─ MyReviewActivity.java     내 리뷰 목록 화면
├─ MyPageActivity.java       마이페이지 / 로그아웃
├─ db/
│  └─ DBHelper.java          SQLiteOpenHelper, 테이블 생성, 시드, 모든 DB 쿼리
├─ model/
│  ├─ Book.java              책 데이터 모델
│  ├─ Review.java            리뷰 데이터 모델
│  └─ User.java              사용자 데이터 모델
├─ adapter/
│  ├─ BookAdapter.java       책 목록 ListView 어댑터
│  ├─ ReviewAdapter.java     리뷰 목록 ListView 어댑터, 스포일러 처리
│  └─ MyReviewAdapter.java   내 리뷰 ListView 어댑터
└─ util/
   ├─ Session.java           SharedPreferences 로그인 세션
   ├─ Pw.java                SHA-256 비밀번호 해시
   ├─ BottomNav.java         하단 탭 이동 처리
   ├─ KakaoApi.java          카카오 책 검색 REST API 호출과 JSON 파싱
   ├─ Covers.java            drawable 표지와 URL 표지 분기
   └─ ImageLoader.java       URL 이미지 비동기 로딩
```

주요 레이아웃 파일은 아래와 같습니다.

```text
app/src/main/res/layout
├─ activity_login.xml
├─ activity_signup.xml
├─ activity_home.xml
├─ activity_search.xml
├─ activity_book_detail.xml
├─ detail_header.xml
├─ activity_write_review.xml
├─ activity_my_review.xml
├─ activity_my_page.xml
├─ include_bottom_nav.xml
├─ list_item_book.xml
├─ list_item_review.xml
└─ list_item_myreview.xml
```

---

## 7. 카카오 API가 하는 일

카카오 API는 앱의 핵심 DB 기능을 대체하는 것이 아니라, **외부 책 정보를 가져오는 보조 기능**입니다.

사용 위치는 두 곳입니다.

| 위치 | 역할 |
|---|---|
| `SearchActivity` | 사용자가 검색한 키워드로 외부 책 검색 |
| `HomeActivity.enrichCoversFromKakao()` | 시드 책의 표지/출판사/출판연도 보강 |

`KakaoApi.java`는 `HttpURLConnection`으로 `https://dapi.kakao.com/v3/search/book`에 요청을 보내고, 응답 JSON을 직접 파싱해 `Book` 객체 리스트로 바꿉니다. 네트워크는 UI 스레드에서 실행할 수 없기 때문에 `ExecutorService`로 백그라운드에서 호출하고, 결과 반영은 `Handler`로 메인 스레드에서 처리합니다.

카카오 REST API 키는 `local.properties`에 넣습니다.

```properties
KAKAO_REST_KEY=발급받은_REST_API_키
```

`local.properties`는 개인 환경 파일이라 GitHub에 올리지 않습니다.

---

## 8. 수업 범위와 추가 구현

화면과 위젯은 수업에서 다룬 기본 Android 구조를 중심으로 구성했습니다.

- `Activity` 기반 화면 전환
- XML 레이아웃
- `TextView`, `EditText`, `Button`, `ImageView`
- `ListView`
- `RatingBar`
- `CheckBox`
- `AlertDialog`
- `Toast`
- `SharedPreferences`

다만 데모 앱 완성도를 위해 아래 기능은 수업 범위를 넘어 추가 학습으로 구현했습니다.

- `SQLiteOpenHelper` 기반 로컬 DB
- `BaseAdapter`를 이용한 커스텀 목록
- `HttpURLConnection` 기반 카카오 REST API 호출
- `ExecutorService`/`Handler`를 이용한 비동기 처리
- URL 이미지 직접 로딩

---

## 9. 기술 스택

- 언어: Java
- 플랫폼: Android
- 빌드: Gradle Kotlin DSL
- SDK: minSdk 24, targetSdk 36, compileSdk 36
- DB: SQLiteOpenHelper
- 외부 API: Kakao Book Search REST API
- 주요 AndroidX 의존성: AppCompat, Material, Activity, ConstraintLayout
- 이미지/네트워크 전용 외부 라이브러리: 사용하지 않음

---

## 10. 실행 방법

Android Studio로 프로젝트 루트 폴더를 열면 됩니다.

카카오 검색까지 확인하려면 프로젝트 루트의 `local.properties`에 REST API 키를 추가합니다.

```properties
sdk.dir=C\:\\Android\\Sdk
KAKAO_REST_KEY=발급받은_REST_API_키
```

빌드 확인 명령은 아래와 같습니다.

```bash
./gradlew.bat assembleDebug
```

기기나 에뮬레이터에 설치하려면 아래 명령을 사용할 수 있습니다.

```bash
./gradlew.bat installDebug
```


