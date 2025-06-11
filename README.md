가계부 앱 - Android Studio(Java) 오류 및 해결 정리

이 문서는 가계부 앱 프로젝트 개발 중 Android Studio(Java) 환경에서 마주쳤던 주요 오류와 그 해결 과정을 정리한 README 파일입니다.

⸻

1. 타입 불일치 오류 (String vs int)

에러 메시지:

Required type: String
Provided:    int

원인:
	•	setColoredText 메서드가 String 타입 색상 값을 기대하는데, getResources().getColor(...) 또는 ContextCompat.getColor(...)를 사용해 int를 반환했기 때문입니다.

해결:

// 1) setColoredText 시그니처 수정
void setColoredText(TextView tv, String prefix, String text, int color) {
    tv.setText(prefix + text);
    tv.setTextColor(color);
}

// 2) 호출 예시
setColoredText(tvIncome,
               "수입  ",
               "+" + income + "원",
               ContextCompat.getColor(this, android.R.color.holo_blue_dark));


⸻

2. Gradle 설정 오류 (unknown property ‘url’)

에러 메시지:

Could not set unknown property 'url'
for repository container of type DefaultRepositoryHandler.

원인:
	•	build.gradle 또는 settings.gradle에서 maven(url = "...") 문법이 잘못 사용되어 발생했습니다.

해결:

// settings.gradle 또는 build.gradle(Project)
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}


⸻

3. 의존성 해결 실패 오류

에러 메시지:

Could not resolve all files for configuration ':app:debugRuntimeClasspath'.

원인:
	•	저장소(repository) 또는 의존성 선언 누락/오타, 혹은 네트워크 이슈.

해결:
	1.	settings.gradle 및 build.gradle(Project)에 google(), mavenCentral(), maven { url 'https://jitpack.io' } 포함
	2.	app/build.gradle에 필요한 의존성 추가

implementation 'com.github.PhilJay:MPAndroidChart:3.1.0'


	3.	Android Studio에서 Sync Project with Gradle Files 실행 및 캐시 클리어

⸻

4. AAPT 리소스 참조 오류

에러 메시지:

error: resource color/ic_launcher_background not found

원인:
	•	mipmap-anydpi-v26/ic_launcher.xml의 <background android:drawable="@color/ic_launcher_background"/>에서 참조하는 리소스가 없음.

해결:
	1.	res/color/ic_launcher_background.xml 파일 생성

<?xml version="1.0" encoding="utf-8"?>
<color name="ic_launcher_background">#FFFFFF</color>


	2.	Adaptive Icon XML에서 정상 참조 확인

⸻

5. 폰트 리소스 타입 오류

에러 메시지:

error: resource type font not found.
No resource identifier found for attribute fontFamily

원인:
	•	res/font 디렉터리 미생성 또는 compileSdkVersion이 26 미만.

해결:
	1.	app/src/main/res/font/ 디렉터리 생성 후 .ttf/.otf 파일 추가
	2.	build.gradle(Module: app)에 compileSdkVersion 31 이상 설정 및

implementation "androidx.core:core-ktx:1.7.0"


	3.	XML에서 폰트 참조

<TextView
    ...
    android:fontFamily="@font/your_font_name" />



⸻

6. 프로젝트 적용 시 주의사항

- 또한 프로젝트 적용시 빌드파일을 제외한 activity, drawable, layout, font(없으면 파일생성), values 파일을 새로운 프로젝트 생성 후 각 위치에 넣주고

JitPack 리포지토리 추가:

settings.gradle에 maven { url 'https://jitpack.io' }를 추가해야 합니다.

GitHub 기반 라이브러리(예: MPAndroidChart)를 사용할 때 JitPack을 통해 안정적으로 의존성을 가져올 수 있습니다.

MPAndroidChart 의존성 선언:

build.gradle(:app)의 dependencies 블록에 다음을 추가합니다:

implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

차트 기능(파이차트, 바차트 등)을 프로젝트에 통합하기 위해 반드시 선언해야 합니다.

의존성 버전 관리:

외부 의존성을 업데이트할 때 버전 호환성을 확인하고, 변경 시 반드시 Gradle 싱크 및 테스트를 실행하세요.

Gradle 캐시 클리어:

의존성 문제 발생 시 File > Invalidate Caches / Restart를 통해 캐시를 초기화한 후 다시 빌드해 보세요.

