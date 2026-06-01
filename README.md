# BookLog — 도서 리뷰 앱

> **"책을 읽고, 기록하고, 나누다"**
> 읽은 책에 별점·한줄평을 남기고, 다른 독자들의 집계 평점을 함께 보는 안드로이드 도서 리뷰 앱.
> 모바일 프로그래밍 기말 프로젝트 (Android · Java · SQLite · 카카오 책 검색 API)

---

## 1. 프로젝트 개요

스팀의 게임 평점, 앱스토어의 앱 리뷰처럼 **실제 사용자가 직접 만들어가는 집계 별점**이 핵심입니다.
서버 없이 **SQLite 로컬 저장소**만으로 동작하며, 배포 없이 개발/시연 환경에서 완전히 작동합니다.

| 기능 | 설명 |
|------|------|
| ⭐ 별점 & 한줄평 | 읽은 책에 1~5점 별점과 한 줄 감상을 남김 |
| 📊 평균 별점 집계 | 모든 리뷰의 별점을 평균 내어 책 상단에 표시 |
| 🙈 스포일러 블라인드 | 스포일러 리뷰는 가려지고, 탭 후 다이얼로그 확인 시 펼침 |
| 📋 내 리뷰 모아보기 | 로그인한 사용자가 쓴 리뷰를 한 화면에 모아봄 |
| 🔎 카카오 책 검색 | 카카오 책 검색 API로 외부 도서를 찾아 리뷰 작성 |
| 👤 로그인/회원가입 | 로컬 계정 + SharedPreferences 세션 |

---

## 2. 기술 스택 / 환경

- **언어**: Java
- **빌드**: Gradle (Kotlin DSL), Android Gradle Plugin
- **SDK**: minSdk 24 / targetSdk 36 / compileSdk 36, Java 11
- **DB**: `SQLiteOpenHelper` (직접 SQL)
- **외부 API**: 카카오 책 검색 REST API
- **외부 라이브러리 없음** (이미지 로딩도 직접 구현 — 수업 범위 준수)

### ⚠️ 구현 규칙 (반드시 지킬 것)

이 프로젝트는 **수업 2·4·5·6·7·8장 범위의 위젯으로만** 구현하는 것이 채점 기준과 직결됩니다.
아래를 "현대화"하지 마세요:

- 목록은 `ListView` + `BaseAdapter` 만 사용 — **`RecyclerView` 금지**
- DB는 `SQLiteOpenHelper` + 직접 SQL — **Room 금지**
- 화면 전환은 일반 `Activity` — **Fragment / Navigation 컴포넌트 금지**
- 표준 위젯(`RatingBar`, `Spinner`, `CheckBox`, `AlertDialog` 등) 위주

---

## 3. 처음 받아서 실행하기 (협업자용)

> 🔰 **git이 처음이라면** — 클론·pull·commit·push 등 단계별 설명은 [GIT_가이드.md](GIT_가이드.md)를 먼저 보세요.

1. **저장소 클론 후 Android Studio로 열기** (프로젝트 루트: 이 폴더)
2. **`local.properties` 작성** — 이 파일은 git에 올라가지 않으므로 직접 만들어야 합니다.
   파일이 없으면 Android Studio가 SDK 경로를 자동 생성해 줍니다. 거기에 카카오 키 한 줄을 추가하세요:
   ```properties
   sdk.dir=C\:\\Android\\Sdk        # 본인 SDK 경로 (보통 자동 생성됨)
   KAKAO_REST_KEY=발급받은_REST_API_키
   ```
   - 카카오 키는 [developers.kakao.com](https://developers.kakao.com) → 애플리케이션 추가 → **REST API 키**.
   - **키가 없어도 앱은 실행됩니다.** 검색 화면에서만 "키 미설정" 안내가 뜨고, 나머지 기능은 정상 작동합니다.
3. **빌드 & 실행**
   ```bash
   ./gradlew.bat assembleDebug     # APK 빌드 (컴파일 오류 점검용)
   ./gradlew.bat installDebug      # 실행 중인 에뮬레이터/기기에 설치
   ```
   또는 Android Studio의 ▶ Run.

### 데모 계정

시드 데이터에 데모 계정이 들어 있습니다:

| 아이디 | 비밀번호 | 비고 |
|--------|----------|------|
| `demo` | `demo` | 닉네임 "김준영", 내 리뷰 2건 보유 |

회원가입으로 새 계정을 만들면 내 리뷰는 빈 상태로 시작합니다.

> 비밀번호는 SHA-256 해시로 저장됩니다(평문 저장 안 함).

---

## 4. 화면 구성 / 흐름

```
LoginActivity (런처)
  ├─ 세션 있으면 → HomeActivity 로 바로 이동
  └─ SignupActivity (회원가입)

[하단 탭 — 각 Activity가 공용 하단바를 include]
  HomeActivity      홈: 검색바 + 인기 도서 목록
  MyReviewActivity  리뷰: 내가 쓴 리뷰 모아보기
  MyPageActivity    마이페이지: 프로필 / 통계 / 로그아웃

[탭 위에 쌓이는 화면 — 자체 뒤로가기]
  BookDetailActivity  책 상세 + 평균별점 카드 + 정렬 + 리뷰목록 + 스포일러 + FAB
  WriteReviewActivity 리뷰 작성 (별점/한줄평/스포일러 체크/저장)
  SearchActivity      카카오 책 검색 결과
```

- 하단 탭은 `res/layout/include_bottom_nav.xml`을 각 탭 화면이 `<include>` 하고,
  `util/BottomNav.setup(activity, 현재탭)`으로 활성 색상과 클릭 이동을 연결합니다.
  탭을 바꾸면 현재 Activity를 `finish()` 하므로 백스택에 탭 화면은 하나만 유지됩니다.

---

## 5. 코드 구조

패키지 루트: `com.example.finalproject`

```
com.example.finalproject
├─ *Activity.java        각 화면 (루트에 위치)
├─ db/DBHelper.java      SQLite 헬퍼 — 테이블/시드/모든 쿼리 (싱글톤)
├─ model/                Book, Review, User (public 필드 POJO)
├─ adapter/              BookAdapter, ReviewAdapter, MyReviewAdapter (BaseAdapter)
└─ util/
   ├─ Session.java       로그인 세션 (SharedPreferences)
   ├─ Pw.java            비밀번호 SHA-256 해시
   ├─ BottomNav.java     하단 탭 연결 헬퍼
   ├─ Covers.java        표지 로딩 분기 (drawable 이름 vs URL)
   ├─ ImageLoader.java   URL 이미지 비동기 로딩 (라이브러리 없이)
   └─ KakaoApi.java      카카오 책 검색 호출 + JSON 파싱
```

데이터 접근은 **DAO/Repository 계층 없이** 각 Activity가 `DBHelper.get(this).xxx()`를 직접 호출합니다.

### DB 스키마 (3 테이블)

| 테이블 | 주요 컬럼 |
|--------|-----------|
| `users` | user_id, username(UNIQUE), pw_hash, nickname |
| `books` | book_id, title, author, publisher, pub_year, genre, page_count, cover, tagline, **base_count, base_sum** |
| `reviews` | review_id, book_id, user_id, nickname, rating, content, is_spoiler, helpful_count, created_at |

### 핵심 설계 — 집계 별점 (꼭 이해할 것)

각 책은 `base_count`(기존 커뮤니티 리뷰 수)와 `base_sum`(별점 합)을 가집니다.
화면에 보이는 평균/리뷰수는:

```
평균   = (base_sum + 실제 리뷰 별점 합) / (base_count + 실제 리뷰 수)
리뷰수 = base_count + 실제 리뷰 수
```

→ 사용자가 리뷰를 남기면 **평균 별점이 실제로 움직입니다.**
관련 쿼리는 `DBHelper.BOOK_AGG_SELECT`. 책 관련 쿼리를 고칠 때 이 base+실데이터 합산 구조를 유지하세요.

- 시드(커뮤니티) 리뷰는 `user_id = 0`.
- **"내 리뷰"** = `reviews WHERE user_id = 로그인한 사용자 id`.

### 스포일러 처리

`ReviewAdapter`가 담당. 스포일러 리뷰는 블라인드 오버레이로 가려지고, 탭하면 `AlertDialog`가 뜨며,
확인 시 `Review.revealed`(런타임 플래그)를 켜고 목록을 갱신합니다.

### 표지 이미지

`util/Covers.load(imageView, cover)`가 분기합니다:
- `cover`가 **drawable 이름**이면 → `res/drawable`에서 해당 리소스(없으면 `cover_placeholder`)
- `cover`가 **http로 시작하는 URL**이면 (카카오 썸네일) → `ImageLoader`가 비동기 로딩

> 현재 시드 책 5권의 표지는 모두 플레이스홀더입니다. 실제 표지를 넣으려면
> `res/drawable`에 이미지를 추가하고 `DBHelper`의 시드 `cover` 값을 그 파일명으로 바꾸세요.

---

## 6. 디자인 토큰

- 색상/치수는 `res/values/colors.xml`, `dimens.xml`에 중앙화 (크림 배경, 앰버 별, 브라운 포인트)
- 라운드 카드/칩/버튼 배경은 `bg_*` 셰이프 drawable
- 별점은 `RatingBar.Amber.Small`(읽기전용) / `RatingBar.Amber.Input`(입력) 스타일
- 다크모드에서도 크림 라이트 톤 유지 (`values-night/themes.xml`이 라이트 테마를 그대로 미러)
- UI 문자열은 한국어, `res/values/strings.xml`에 모음

---

## 7. 자주 쓰는 명령

```bash
./gradlew.bat assembleDebug          # 디버그 APK 빌드 (오류 점검)
./gradlew.bat installDebug           # 에뮬레이터/기기에 설치
./gradlew.bat test                   # JVM 단위 테스트
./gradlew.bat connectedAndroidTest   # 계측 테스트 (기기/에뮬 필요)
```

UI 동작은 헤드리스로 검증되지 않으므로 **반드시 에뮬레이터/기기에서 직접 확인**하세요.

---

## 8. 협업 메모

- 브랜치: 작업은 `develop`, 안정 버전은 `main`.
- `local.properties`는 git에 올리지 않습니다(각자 SDK 경로/카카오 키가 다름). 새로 받으면 직접 작성하세요.
- 패키지/위젯 선택 시 **3장 상단의 구현 규칙(ListView/SQLite/Activity)** 을 지켜주세요.
