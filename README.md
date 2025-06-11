# 가계부 앱

## Android Studio(Java) 오류 및 해결 정리

가계부 앱 프로젝트 개발 중 Android Studio(Java) 환경에서 마주쳤던 주요 오류와 해결 과정을 정리한 README 파일입니다.

---

### 1. 타입 불일치 오류 (String vs int)

* **에러 메시지**:

  ```
  Required type: String
  Provided:    int
  ```
* **원인**:

  * `setColoredText` 메서드가 `String` 타입 색상 값을 기대하는데, `getResources().getColor(...)` 또는 `ContextCompat.getColor(...)`가 `int`를 반환해서 발생합니다.
* **해결 코드**:

  ```java
  // 1) setColoredText 시그니처 수정
  void setColoredText(TextView tv, String prefix, String text, int color) {
      tv.setText(prefix + text);
      tv.setTextColor(color);
  }

  // 2) 호출 예시
  setColoredText(
      tvIncome,
      "수입  ",
      "+" + income + "원",
      ContextCompat.getColor(this, android.R.color.holo_blue_dark)
  );
  ```

---

### 2. Gradle 설정 오류 (unknown property 'url')

* **에러 메시지**:

  ```
  Could not set unknown property 'url'
  for repository container of type DefaultRepositoryHandler.
  ```
* **원인**:

  * `settings.gradle` 또는 `build.gradle(Project)`에서 `maven(url = "...")` 문법이 잘못 사용되었습니다.
* **해결 코드**:

  ```groovy
  // settings.gradle (Project)
  dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
      google()
      mavenCentral()
      maven { url 'https://jitpack.io' }  // JitPack 리포지토리 추가
    }
  }
  ```

---

### 3. 의존성 해결 실패 오류

* **에러 메시지**:

  ```
  Could not resolve all files for configuration ':app:debugRuntimeClasspath'.
  ```
* **원인**:

  * 리포지터리 누락, 의존성 선언 오타, 네트워크 이슈 등
* **해결 과정**:

  1. **리포지터리 설정**

     * `settings.gradle` 및 `build.gradle(Project)`에 `google()`, `mavenCentral()`, `maven { url 'https://jitpack.io' }` 포함
  2. **의존성 추가** (`app/build.gradle`)

     ```groovy
     dependencies {
       implementation "com.github.PhilJay:MPAndroidChart:3.1.0"
     }
     ```
  3. **빌드 동기화**

     * Android Studio에서 **Sync Project with Gradle Files** 실행
     * 문제가 지속되면 **Invalidate Caches / Restart**

---

### 4. AAPT 리소스 참조 오류

* **에러 메시지**:

  ```
  error: resource color/ic_launcher_background not found
  ```
* **원인**:

  * `mipmap-anydpi-v26/ic_launcher.xml`에서 참조하는 `@color/ic_launcher_background` 리소스가 없음
* **해결 방법**:

  1. `res/color/ic_launcher_background.xml` 파일 생성

     ```xml
     <?xml version="1.0" encoding="utf-8"?>
     <color name="ic_launcher_background">#FFFFFF</color>
     ```
  2. Adaptive Icon XML에서 정상 참조 확인

---

### 5. 폰트 리소스 타입 오류

* **에러 메시지**:

  ```
  error: resource type font not found.
  No resource identifier found for attribute fontFamily
  ```
* **원인**:

  * `res/font` 디렉터리 미생성 또는 `compileSdkVersion`이 26 미만
* **해결 단계**:

  1. `app/src/main/res/font/` 디렉터리 생성 후 `.ttf`/`.otf` 파일 추가
  2. `build.gradle(Module: app)` 설정

     ```groovy
     android {
       compileSdkVersion 31  // 최소 26 이상
     }
     dependencies {
       implementation "androidx.core:core-ktx:1.7.0"
     }
     ```
  3. XML에서 폰트 참조

     ```xml
     <TextView
       ...
       android:fontFamily="@font/your_font_name" />
     ```

---

## 프로젝트 적용 시 주의사항

* **리소스 파일 통합**:

  * activity, drawable, layout, font(없으면 생성), values 디렉터리의 리소스를 새 프로젝트에 복사
* **JitPack 리포지토리 추가**:

  * `settings.gradle`에 아래를 추가해야 합니다.

    ```groovy
    maven { url 'https://jitpack.io' }
    ```
  * GitHub 기반 라이브러리(예: MPAndroidChart) 의존성 해결에 필요합니다.
* **MPAndroidChart 의존성 선언**:

  * `app/build.gradle` → `dependencies` 블록에 아래 추가

    ```groovy
    implementation "com.github.PhilJay:MPAndroidChart:3.1.0"
    ```
* **의존성 버전 관리**:

  * 외부 라이브러리 버전 업데이트 시 호환성 확인 및 빌드/테스트 실행
* **캐시 초기화**:

  * 의존성 문제 발생 시 Android Studio에서 **Invalidate Caches / Restart** 수행

---

문서를 참고하여 프로젝트에 적용하면 유사한 문제를 빠르게 해결할 수 있습니다.
추가 오류나 개선 사항이 있다면 README를 업데이트하세요.
