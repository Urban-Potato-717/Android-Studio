# Git 초보자 가이드 (BookLog 협업용)

git을 처음 쓰는 팀원을 위한 단계별 설명입니다. **순서대로 따라 하면** 내 컴퓨터에서 프로젝트를 받아 실행하고, 변경한 내용을 팀에 올릴 수 있습니다.

> 용어 한 줄 정리
> - **저장소(repository)**: 코드가 보관된 곳. 우리 저장소는 GitHub의 `Urban-Potato-717/Android-Studio`.
> - **클론(clone)**: GitHub에 있는 저장소를 내 컴퓨터로 처음 복사해 오는 것.
> - **pull**: 다른 사람이 올린 최신 변경을 내 컴퓨터로 내려받는 것.
> - **commit**: 내가 바꾼 내용을 "기록"으로 묶는 것 (아직 내 컴퓨터에만 있음).
> - **push**: 내 커밋을 GitHub에 올려 팀과 공유하는 것.
> - **branch(브랜치)**: 작업 갈래. 우리는 `develop`에서 같이 작업합니다.

---

## 0. 처음 한 번만 — 설치와 설정

### (1) Git 설치
- Windows: [git-scm.com](https://git-scm.com/download/win) 에서 다운로드 후 기본값으로 설치.
- 설치 확인: `cmd` 또는 PowerShell을 열고
  ```bash
  git --version
  ```
  버전이 나오면 성공.

### (2) 내 이름/이메일 등록 (커밋에 기록됨)
```bash
git config --global user.name "본인이름"
git config --global user.email "본인깃허브이메일@example.com"
```
> GitHub 가입 이메일과 같게 하면 좋습니다.

### (3) GitHub 계정 & 협업자 등록
- GitHub 계정이 없으면 [github.com](https://github.com) 에서 가입.
- 저장소 주인(준영)에게 **본인 GitHub 아이디를 알려주고 Collaborator로 초대**받으세요.
  (초대 메일의 수락 버튼을 눌러야 push가 가능합니다.)

---

## 1. 프로젝트 받아오기 (clone) — 처음 한 번만

코드를 둘 폴더로 이동한 뒤(예: `C:\Android\Projects`), 아래를 실행합니다.
```bash
cd C:\Android\Projects
git clone https://github.com/Urban-Potato-717/Android-Studio.git
```
> 처음 clone 시 GitHub 로그인 창이 뜨면 본인 계정으로 로그인하세요.

폴더가 생기면 그 안으로 들어갑니다.
```bash
cd Android-Studio
```

### 우리가 작업하는 브랜치로 전환
기본 브랜치는 `main`이지만, **실제 작업은 `develop`에서** 합니다.
```bash
git checkout develop
```

---

## 2. 내 컴퓨터에서 실행되게 만들기 (중요)

이 프로젝트는 보안상 **`local.properties` 파일을 GitHub에 올리지 않습니다.**
그래서 clone 직후에는 이 파일이 없을 수 있고, **직접 만들어야** 합니다.

1. Android Studio로 `Android-Studio` 폴더를 엽니다. (보통 SDK 경로가 자동 생성됨)
2. 프로젝트 루트의 `local.properties` 파일을 열어 아래처럼 만듭니다.
   ```properties
   sdk.dir=C\:\\Android\\Sdk
   KAKAO_REST_KEY=발급받은_카카오_REST_API_키
   ```
   - `sdk.dir`은 본인 Android SDK 경로 (대개 자동으로 채워져 있음).
   - 카카오 키는 검색 기능에만 필요. 없으면 비워둬도 앱은 실행됩니다.
3. ▶ Run 버튼으로 에뮬레이터/휴대폰에서 실행.

> 자세한 설정과 데모 계정(`demo`/`demo`)은 [README.md](README.md) 참고.

---

## 3. 매일 작업하는 순서 (가장 중요!)

### ⭐ 일을 시작하기 전 — 항상 최신 코드부터 받기 (pull)
```bash
git checkout develop      # develop 브랜치인지 확인
git pull origin develop   # 팀원이 올린 최신 변경 내려받기
```
> **작업 시작 전 pull은 습관처럼** 하세요. 안 하면 나중에 충돌이 생기기 쉽습니다.

### 작업 → 저장(commit) → 공유(push)

1. 코드를 수정합니다. (Android Studio에서 편집)

2. 무엇이 바뀌었는지 확인:
   ```bash
   git status
   ```

3. 바뀐 파일을 "담기"(stage):
   ```bash
   git add .
   ```
   > `.` 은 "바뀐 것 전부"라는 뜻입니다.

4. 기록 남기기(commit) — 메시지는 무엇을 했는지 한 줄로:
   ```bash
   git commit -m "홈 화면 검색 버그 수정"
   ```

5. GitHub에 올리기(push):
   ```bash
   git push origin develop
   ```

이제 다른 팀원이 `git pull` 하면 내 변경을 받게 됩니다.

---

## 4. 자주 쓰는 명령 요약

| 하고 싶은 것 | 명령어 |
|--------------|--------|
| 지금 상태 보기 | `git status` |
| 최신 코드 받기 | `git pull origin develop` |
| 바뀐 것 담기 | `git add .` |
| 기록 남기기 | `git commit -m "메시지"` |
| GitHub에 올리기 | `git push origin develop` |
| 어떤 브랜치인지 보기 | `git branch` |
| develop으로 이동 | `git checkout develop` |
| 최근 커밋 기록 보기 | `git log --oneline` |

---

## 5. 초보가 자주 겪는 상황

### "push 했더니 거부당했어요 (rejected)"
대부분 **팀원이 먼저 올린 변경을 내가 안 받아서** 생깁니다. 먼저 pull 하세요:
```bash
git pull origin develop
git push origin develop
```

### "충돌(conflict)이 났어요"
같은 파일의 같은 부분을 두 사람이 동시에 고치면 발생합니다.
1. 충돌난 파일을 Android Studio로 엽니다.
2. `<<<<<<<`, `=======`, `>>>>>>>` 표시가 보입니다.
   - `<<<<<<<` ~ `=======` : 내 코드
   - `=======` ~ `>>>>>>>` : 팀원 코드
3. 둘 중 맞는 쪽만 남기고 그 표시줄들은 지웁니다.
4. 정리 후:
   ```bash
   git add .
   git commit -m "충돌 해결"
   git push origin develop
   ```
> 혼자 해결이 어려우면 **고치지 말고 팀원과 함께** 보는 게 안전합니다.

### "실수로 잘못 바꿨어요. 아직 commit 전이에요."
특정 파일을 마지막 저장 상태로 되돌리기:
```bash
git checkout -- 파일이름
```
> 이건 되돌리면 복구가 안 되니 **확실할 때만** 사용하세요.

### "내가 지금 뭘 했는지 모르겠어요"
당황하지 말고 먼저:
```bash
git status
```
대부분의 답은 `git status` 안내 메시지에 들어 있습니다.

---

## 6. 안전 수칙

- **작업 전 항상 `git pull`** → 작업 후 `add → commit → push` 순서를 지키세요.
- `local.properties` 안의 카카오 키 같은 **개인 정보는 절대 커밋하지 마세요.** (이미 `.gitignore`로 막혀 있음)
- `git push --force`, `git reset --hard` 같은 **위험한 명령은 혼자 쓰지 말고** 팀원과 상의하세요.
- 막히면 에러 메시지를 그대로 복사해서 물어보면 됩니다.
