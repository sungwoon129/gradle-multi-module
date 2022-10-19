#### Gradle 멀티 모듈 관리 ####
------
안녕하세요. 오늘은 gradle 을 활용해서 여러개의 모듈을 관리하는 방법인 멀티 모듈에 대해서 공부하려고 합니다.

모듈은 오라클 자바 문서에서 다음과 같이 정의하고 있습니다.
> 모듈은 패키지의 한 단계 위의 집합체이며, 관련된 패키지와 리소스들의 재사용할 수 있는 그룹

조금 더 직관적으로 표현하면 프로젝트 하위의 특정한 패키지와 리소스들을 하나의 단위로 묶은 것이라고 볼 수 있습니다.
이것들을 묶어서 빌드하여 jar 파일로 만들어 다른 프로젝트의 라이브러리 처럼 활용할 수도 있고, 하나의 프로젝트 안에서 불러와
코드관리에 도움을 주기도 합니다. 

프로젝트의 규모가 커지고 서비스가 복잡해질수록 단일 프로젝트로 구성되는 경우는 많지 않습니다.
우리가 흔히 접하는 서비스들도 여러개의 모듈을 두어 관리하고 있는 경우가 많습니다. 기본적으로, 서비스의 규모가 크지 않더라도 일정 수준 이상의
트래픽을 감당하려면 일반 사용자의 요청을 처리하는 서버와 DB 와 연결하여 데이터를 관리하는 서버로 나누어 구성해야합니다.
이외의 경우에도 웹서버를 개발하는 과정에서 통계기능이 필요하여 수많은 데이터를 다루기엔 트래픽이 부담스러워 Batch 서버를 구분하여 구성하는 경우도
있습니다.

이렇게 프로젝트를 구성하게 되면 고민되는 부분이 있습니다. 

> 웹서버와 Batch 서버 모두에서 사용되는 클래스들은 어떻게 처리하지?

![multi-module-img1](https://user-images.githubusercontent.com/43958570/196709348-15a7a288-705e-40b8-a2e0-40e12b5cab96.png)

예를들어, web 서버와 batch 서버 모두 Member 클래스를 활용하고 싶다고 한다면 어떤 방법이 있을까요?
두 모듈은 서로 구분되어 있기 때문에 Web 서버 내부에 존재하는 Member 클래스에 batch 서버에서는 접근할 수 없습니다.

쉽게 생각할 수 있는 방법은 **복사&붙여넣기** 입니다.
web 서버에서 Member 클래스를 만들고 batch 서버에 동일한 Member 클래스를 붙여넣기 하는 방법입니다. 
하지만 이 방법은 단점이 많습니다. Member 클래스를 이용하는 모듈이 늘어나게 되면 복사&붙여넣기 해야하는 횟수도 늘어나고
Member 클래스에 변경이 생겼을 때, 복사한 모든 Member 클래스의 코드를 수정해야 하고, 개발자가 실수할 여지도 많습니다.

조금 더 나은 방법을 고민해보면 좋을 것 같습니다.
하나의 Member 클래스를 공통으로 서로 다른 모듈 이곳저곳에서 사용할 수 있으면 좋지 않을까요?
그렇게 되면 하나의 Member 클래스만 수정해도 Member 클래스를 이용하는 모든 모듈에도 수정사항이 반영될테니까요.
별도의 프로젝트를 만들어 Member 클래스를 생성한 후 접근할 수 있는 api 를 만들어 빌드한 jar 파일을 web 서버와 batch 서버에 라이브러리로 등록하는 건 어떨까요?
이렇게 하면 라이브러리 파일만 수정하면 되니까 여러 Member 클래스를 수정할 필요는 없을 것 같습니다.
하지만 Member 클래스에 수정이 필요할 때마다 member 라이브러리를 매번 다시 빌드하고 프로젝트에 포함시키는 번거로운 과정이 생겼습니다.

개발할 때 쉽게 불러와서 사용하고, 빌드할때는 자동으로 프로젝트에 포함되어 빌드되는 방법이 있으면 좋을 것 같습니다.
추가로 클래스 단위가 아니라 공통 프로젝트를 두어 공통으로 사용되는 프로젝트를 구성하여 활용할 수 있다면 더 좋을 것 같습니다.

Gradle의 Multi Module 방식을 사용하면 고민을 해결할 수 있습니다.
Multi Module은 프로젝트 간의 의존관계를 정의하여, 하위 의존관계를 가진 프로젝트에 접근할 수 있도록 하고 빌드 할 때 자동으로 포함하여 함께 빌드해주는 기능을 제공합니다.

#### 프로젝트 구성 ####
----------------------
![gradle-multi-module-img2](https://user-images.githubusercontent.com/43958570/196719023-91878153-dea2-4532-a39a-f3250915a7a8.png)

Gradle로 새로운 프로젝트를 생성합니다. 여기서는 gradle-multi-module 이 ROOT 프로젝트가 됩니다.

![gradle-multi-module-img3](https://user-images.githubusercontent.com/43958570/196720415-f15776a9-cc4a-4ff8-abf3-2ead6ecb36b4.png)
그리고 ROOT 프로젝트 이름에서 우클릭 한 후 **New -> Module...** 을 클릭하여 Gradle을 선택하고 모듈들을 생성해줍니다.
빌드는 항상 ROOT 프로젝트를 기준으로 수행하기 때문에 모듈들에는 gradle 폴더 및 gradlew, gradlew.bat 파일이 존재하지 않습니다.
각각의 모듈에는 빌드를 어떻게 할 것인지에 대한 설정파일인 build.gradle 과 소스코드가 있는 src 폴더만 존재합니다.
ROOT 프로젝트인 gradle-multi-module 은 각각의 모듈들을 묶어서 관리하는 역할만 하므로 src 폴더가 필요하지 않으니 삭제해줍니다.

### settings.gradle ###
![gradle-multi-module-img4](https://user-images.githubusercontent.com/43958570/196724367-5b5e43af-9e5e-4f11-ac3e-b96f70fed2d7.png)

settings.gradle 을 열어보면 현재 ROOT 프로젝트에서 하위 모듈로 어떤 프로젝트들을 관리하고 있는지 명시되어 있습니다. 
위 코드는 gradle-multi-module이 module-core, module-web, module-batch 를 하위 모듈로 관리하고 있다는 의미입니다.

``` groovy
// plugins 는 미리 구성해놓은 task 들의 그룹이며 특정 빌드과정에 필요한 기본정보를 포함하고 있음
plugins {
    // Spring Boot Gradle 플러그인으로 사용하면 Spring Boot 종속성을 관리하고 Gradle을 빌드 도구로 사용할 때 애플리케이션을 패키징하고 실행할 수 있습니다.
    // 단독으로 사용되는 경우 프로젝트에 거의 영향을 주지 않습니다.
    // 예를 들어 java 플러그인과 함께 적용되면 실행 가능한 jar 빌드 작업이 자동으로 구성됩니다.
    // spring-boot-dependencies를 통해서 의존성 관리 기능을 제공하기도 합니다.

    id 'org.springframework.boot' version '2.7.4'
    id 'java'
}

repositories {
    mavenCentral()
}


// bootJar 작업은 실행 가능한 jar을 생성하려고 시도하기 때문에 이를 위해서는 main() 메서드가 필요합니다.
// Root 프로젝트는 main 없이 라이브러리의 역할을 하는 모듈이기 때문에 false로 비활성화해줍니다.
bootJar.enabled = false

// settings.gradle 에 명시된 include 프로젝트 모두에 대한 공통사항 정의(상위 루트 제외)
subprojects {
    group = 'com.blog'
    version = '0.0.1-SNAPSHOT'
    sourceCompatibility = '11'

    // subprojects 블록 안에서는 plugins 블록을 사용할 수 없어, 플러그인 등록을 위해서는 apply plugin 을 사용해야함

    apply plugin: 'java'
    // build.gradle 에서 api() 를 사용하려면 java-library 플러그인 적용필요
    apply plugin: 'java-library'
    apply plugin: 'org.springframework.boot'
    // spring boot dependency 플러그인을 적용하여 사용중인 부트 버전에서 자동으로 의존성을 가져옴
    apply plugin: 'io.spring.dependency-management'

    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }

    repositories {
        mavenCentral()
    }

    // 관리하는 모듈에 공통 dependencies
    dependencies {
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }

    test {
        useJUnitPlatform()
    }
}
```















