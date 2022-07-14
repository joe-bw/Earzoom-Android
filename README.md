# 이어줌 안드로이드 프로젝트
 - firefox 웹브라우저를 기반으로 하여 웹브라우저를 통해 재생되는 영상을 추출하여 음성을 텍스트로(STT) 변환하여 청각장애인을 위한 서비스를 제공하고 있습니다.
 - 추가로 수어 아바타를 통해 자막에 대한 수어를 아바타를 통해 내용 설명을 제공하고 있습니다.


- 개발 이슈: Firefox 웹브라우저를 기반으로 개발함에 있어 Firefox 웹 브라우저 소스의 개발 환경에 따라 개발을 진행합니다. 최신 버전의 환경설정을 하지 못하게 되는 아쉬움이 있습니다.
 
----------

# 🚧개발환경


## 개발도구

- Android Studio 최신 버전


      * Update 22/05/16            
      Android Studio Chipmunk | 2021.2.1
      Build #AI-212.5712.43.2112.8512546, built on April 29, 2022
      Runtime version: 11.0.12+7-b1504.28-7817840 amd64
      VM: OpenJDK 64-Bit Server VM by Oracle Corporation
      Windows 10 10.0
      GC: G1 Young Generation, G1 Old Generation
      Memory: 3072M
      Cores: 8
      Registry: external.system.auto.import.disabled=true, ide.balloon.shadow.size=0
      Non-Bundled Plugins: 
               com.github.patou.gitmoji (1.10.0), 
               idea.plugin.protoeditor (212.5080.8), 
               org.intellij.plugins.markdown (212.5457.16), 
               mobi.hsz.idea.gitignore (4.3.0), 
               org.jetbrains.kotlin (212-1.6.21-release-334-AS5457.46), 
               com.chrisrm.idea.MaterialThemeUI (6.9.1)
      
----------

## 보안

- API

      EncryptedSharedPreferences
          spec: MasterKeys.AES256_GCM_SPEC
     

----------

## Gradle JDK

- JDK 11

      version 11.0.11
      
      Android Gradle Plugin Version: 7.1.2
      
      Gradle Versioni: 7.2

----------
## 개발 언어

- Kotlin, Java

----------
## 대상 단말

      안드로이드 6.0 마쉬맬로우(Marshmallow) 버전 이상의 phone / tablet 장치
      API LV 21이 default 이나, 보안 및 개발 편의 성을 이유로 API LV 23으로 설정하여 진행 예정
      
----------
## 개발 설정

      Min SDK Version: 23
       - Android 6.0(Marshmallow) 부터 지원
      
      Target SDK Version: 30
       - Android 11(R) 을 지원하는 것으로, Google play에 배포를 위해서는 반드시 30 이상으로 설정을 해야만 하기에 강제적입니다.
       - 이로 인해 로컬 스토리지의 파일 접근 권한이 강해져 개발 이슈가 발생합니다.
       
      Build features: dataBinding 사용
      
-------------
## 디자인 패턴

- MVVM 패턴 기반의 AAC(Android Architecture Component) 사용

      목적: Clean Architecture
      - 테스트와 개발의 편의성 향상, 간결한 소스로 인한 가독성 향상을 위해

      MVVM pattern: Model – View – ViewModel
      
      Libraries
            Android Support Library
            Android Architecture Components
            - LiveData, Flow
            - ViewModel
            - Room
            - Navigation
            Hilt for dependency injection
            Retrofit for REST api communication
            Glide for image loading
            Timber for logging
            Espresso for UI tests
            Mockito for mocking in tests
            
----------





# Firefox Focus for Android

_Browse like no one’s watching. The new Firefox Focus automatically blocks a wide range of online trackers — from the moment you launch it to the second you leave it. Easily erase your history, passwords and cookies, so you won’t get followed by things like unwanted ads._ 

Firefox Focus provides automatic ad blocking and tracking protection on an easy-to-use private browser.

<a href="https://play.google.com/store/apps/details?id=org.mozilla.focus" target="_blank"><img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="90"/></a>

* [Google Play: Firefox Focus (Global)](https://play.google.com/store/apps/details?id=org.mozilla.focus)
* [Google Play: Firefox Klar (Germany, Austria & Switzerland)](https://play.google.com/store/apps/details?id=org.mozilla.klar)
* [Download APKs](https://github.com/mozilla-mobile/focus-android/releases)

## Getting Involved


We encourage you to participate in this open source project. We love Pull Requests, Bug Reports, ideas, (security) code reviews or any other kind of positive contribution. 

Before you attempt to make a contribution please read the [Community Participation Guidelines](https://www.mozilla.org/en-US/about/governance/policies/participation/).

* [Guide to Contributing](https://github.com/mozilla-mobile/shared-docs/blob/main/android/CONTRIBUTING.md) (**New contributors start here!**)

* [View current Issues](https://github.com/mozilla-mobile/focus-android/issues), [view current Pull Requests](https://github.com/mozilla-mobile/focus-android/pulls), or [file a security issue][sec issue].

* Opt-in to our Mailing List [firefox-focus-public@](https://mail.mozilla.org/listinfo/firefox-focus-public) to keep up to date.

* [View the Wiki](https://github.com/mozilla-mobile/focus-android/wiki).

**Beginners!** - Watch out for [Issues with the "Good First Issue" label](https://github.com/mozilla-mobile/focus-android/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22). These are easy bugs that have been left for first timers to have a go, get involved and make a positive contribution to the project!

## Build Instructions


1. Clone or Download the repository:

  ```shell
  git clone https://github.com/mozilla-mobile/focus-android
  ```

2. Import the project into Android Studio **or** build on the command line:

  ```shell
  ./gradlew clean app:assembleFocusDebug
  ```

3. Make sure to select the correct build variant in Android Studio:
**focusArmDebug** for ARM
**focusX86Debug** for X86
**focusAarch64Debug** for ARM64

## local.properties helpers
You can speed up or enhance local development by setting a few helper flags available in `local.properties` which will be made easily available as gradle properties.

### Automatically sign release builds
To sign your release builds with your debug key automatically, add the following to `<proj-root>/local.properties`:

```sh
autosignReleaseWithDebugKey
```

With this line, release build variants will automatically be signed with your debug key (like debug builds), allowing them to be built and installed directly through Android Studio or the command line.

This is helpful when you're building release variants frequently, for example to test feature flags and or do performance analyses.

### Building debuggable release variants

Nightly, Beta and Release variants are getting published to Google Play and therefore are not debuggable. To locally create debuggable builds of those variants, add the following to `<proj-root>/local.properties`:

```sh
debuggable
```

### Auto-publication workflow for android-components and application-services
If you're making changes to these projects and want to test them in Focus, auto-publication workflow is the fastest, most reliable
way to do that.

In `local.properties`, specify a relative path to your local `android-components` and/or `application-services` checkouts. E.g.:
- `autoPublish.android-components.dir=../android-components`
- `autoPublish.application-services.dir=../application-services`

Once these flags are set, your Focus builds will include any local modifications present in these projects.

See a [demo of auto-publication workflow in action](https://www.youtube.com/watch?v=qZKlBzVvQGc).

## Pre-push hooks
To reduce review turn-around time, we'd like all pushes to run tests locally. We'd
recommend you use our provided pre-push hook in `quality/pre-push-recommended.sh`.
Using this hook will guarantee your hook gets updated as the repository changes.
This hook tries to run as much as possible without taking too much time.

To add it, run this command from the project root:
```sh
ln -s ../../quality/pre-push-recommended.sh .git/hooks/pre-push
```

To push without running the pre-push hook (e.g. doc updates):
```sh
git push <remote> --no-verify
```

## License


    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/

[sec issue]: https://bugzilla.mozilla.org/enter_bug.cgi?assigned_to=nobody%40mozilla.org&bug_file_loc=http%3A%2F%2F&bug_ignored=0&bug_severity=normal&bug_status=NEW&cf_fx_iteration=---&cf_fx_points=---&component=Security%3A%20Android&contenttypemethod=autodetect&contenttypeselection=text%2Fplain&defined_groups=1&flag_type-4=X&flag_type-607=X&flag_type-791=X&flag_type-800=X&flag_type-803=X&form_name=enter_bug&groups=firefox-core-security&maketemplate=Remember%20values%20as%20bookmarkable%20template&op_sys=Unspecified&priority=--&product=Focus&rep_platform=Unspecified&target_milestone=---&version=---
