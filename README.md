# Gradle 멀티 모듈 관리 #
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

## 프로젝트 구성 ##
![gradle-multi-module-img2](https://user-images.githubusercontent.com/43958570/196719023-91878153-dea2-4532-a39a-f3250915a7a8.png)

Gradle로 새로운 프로젝트를 생성합니다. 여기서는 gradle-multi-module 이 ROOT 프로젝트가 됩니다.

![gradle-multi-module-img3](https://user-images.githubusercontent.com/43958570/196720415-f15776a9-cc4a-4ff8-abf3-2ead6ecb36b4.png)
그리고 ROOT 프로젝트 이름에서 우클릭 한 후 **New -> Module...** 을 클릭하여 Gradle을 선택하고 모듈들을 생성해줍니다.
빌드는 항상 ROOT 프로젝트를 기준으로 수행하기 때문에 모듈들에는 gradle 폴더 및 gradlew, gradlew.bat 파일이 존재하지 않습니다.
각각의 모듈에는 빌드를 어떻게 할 것인지에 대한 설정파일인 build.gradle 과 소스코드가 있는 src 폴더만 존재합니다.
ROOT 프로젝트인 gradle-multi-module 은 각각의 모듈들을 묶어서 관리하는 역할만 하므로 src 폴더가 필요하지 않으니 삭제해줍니다.

### ROOT settings.gradle ###
![gradle-multi-module-img4](https://user-images.githubusercontent.com/43958570/196724367-5b5e43af-9e5e-4f11-ac3e-b96f70fed2d7.png)

settings.gradle 을 열어보면 현재 ROOT 프로젝트에서 하위 모듈로 어떤 프로젝트들을 관리하고 있는지 명시되어 있습니다. 
위 코드는 gradle-multi-module이 module-core, module-web, module-batch 를 하위 모듈로 관리하고 있다는 의미입니다.

![image](https://user-images.githubusercontent.com/43958570/196844343-0c20af5e-a5bf-4747-bfc3-d91aad136d23.png)


### ROOT build.gradle ###
``` groovy
// plugins 는 미리 구성해놓은 task 들의 그룹이며 특정 빌드과정에 필요한 기본정보를 포함하고 있습니다
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

// settings.gradle 에 명시된 include 프로젝트 모두에 대한 공통사항 정의(루트 제외)
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

    // 관리하는 모듈의 공통 dependencies
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

#### subprojects ####
subprojects 블록안에서 setting.gradle 파일에 작성되어있는 하위 프로젝트들에 대한 설정을 공통으로 적용할 수 있습니다.
dependencies, repositories 등 다른 부분은 멀티 모듈을 사용하지 않는 단일 프로젝트와 동일하게 설정하지만 plugins 블록은 subprojects 블록안에서 사용할 수 없기 때문에 apply plugin을 사용해서 적용시켜야 합니다.

#### bootJar.enabled = false  ####
특별히 설정하지 않고 gradle을 빌드하게 되면 **실행가능한 jar** 파일을 생성을 시도합니다. 이 때, 프로젝트의 시작점 역할을 하는 main 메소드가 필요하지만 gradle-multi-module 프로젝트는
모듈들을 관리하는 역할을 하는 프로젝트이므로 main 메소드가 없기 때문에 bootJar.enabled = false 로 비활성시켜줍니다. 



다음에는 **module-core** 모듈에서는 프로젝트 전체에서 공통적으로 사용하는 domain, repository, domain service를 만들겠습니다.
도메인 서비스는 하나의 트랜잭션 단위를 의미합니다. 도메인 서비스는 도메인 영역에 위치한 도메인 로직을 표현할 때 사용되는 개념입니다.
은행에서 '계좌이체' 라는 도메인이 있고, 계좌이체 서비스를 위해서는 송금하는 계좌와, 돈을 받을 계좌 그리고 금액이 필요합니다. 
계좌이체를 하기 위해서는 두가지 행위가 필요합니다.
> + 송금하는 계좌에서 금액을 차감시킨다.
> + 돈을 받는 계좌에서 금액을 증가시킨다.

```java
public class TransferService {
    public void transferMoney(Account sendAcc, Account goalAccount, Money money) {
        sendAcc.withdraw(money);
        goalAccount.deposit(amounts);
    }
}
```
계좌이체를 하려면 계좌 도메인에서 두 가지 서비스가 필요하고, 각각의 서비스는 트랜잭션으로 처리되어야 합니다.
여기서 "계좌이체"가 도메인 서비스에 해당합니다. 여러 도메인의 개념이 하나의 서비스에서 필요할 때, 하나의 도메인 안에서
억지로 여러 도메인의 개념을 구현하여 사용하지 않고, 별도의 서비스를 만들어 관리하는 것입니다.

Member 도메인 클래스와 MemberJpaRepository 를 만듭니다. 외부 모듈에서 Member DB를 직접 조회하거나 변경하는 행위는 프로젝트 계층구조에 적합하지 않습니다. 외부모듈에서 Member 클래스에 접근은 가능하지만, 외부모듈에서 JpaRepository를 호출하여 
도메인을 직접 조작하면, 이후 Member 도메인에 변화가 생기면 Member 도메인을 변경하는 모든 모듈에도 수정이 필요합니다. 그래서 외부모듈에서는 MemberService를 통해 Member DB에 접근하도록 구성하였습니다.
MemberService는 MemeberRepository에 의존하며, 외부모듈에 Member 데이터를 전달하는 역할을 합니다.
MemberRepository는 도메인 서비스로 JPA 에 의존성이 있어 인터페이스로 추상화하고, infrastructure 영역에서 구현하였습니다.

### module-core Member ###
``` java
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String nickname;
} 
```

### module-core MemberRepository ###
```java
public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);
}
```

### module-core MemberJpaRepository ###
```java
public interface MemberJpaRepository extends JpaRepository<Member,Long> {
}
```

### module-core MemberRepositoryImpl ###
```java
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id);
    }


}
```

### module-core MemberService ###
```java
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void saveAnyMember() {
        memberRepository.save(Member.builder().name("random").build());
    }

    @Transactional
    public Long signup (Member member) {
        return memberRepository.save(member).getId();
    }

    @Transactional
    public Member findAnyMember() {
        return memberRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("해당 id를 가진 회원이 존재하지 않습니다."));
    }

    @Transactional
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id를 가진 회원이 존재하지 않습니다."));
    }
    
}
```

### module-core build.gradle ###
```groovy
bootJar { enabled = false }
jar { enabled = true }

dependencies {
    api 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
}
```

module-core 에서는 entity 클래스와 repository 기능이 필요하고 repository 테스트가 필요하기 때문에 관련 의존성들을 추가해줍니다.
또한 core 모듈은 다른 모듈들에 포함되어 라이브러리와 같은 역할을 하므로 main 메소드가 존재하지 않습니다. 그래서 bootJar { enabled = false } 옵션을 통해 

[이동욱님의 블로그](https://jojoldu.tistory.com/m/123) 에서는 gradle3 버전을 쓰고 있어, readme.md 작성 기준으로 최신버전인 gradle7 에 알맞게 변경하였습니다.
외부모듈에서 module-core의 member 엔티티에 접근하기 위해서 implementation 대신 api를 사용했습니다. api를 사용하면 module-core를 가져오는 모듈에서 또한 해당 라이브러리에 대한 의존성이 추가됩니다.
이 때문에 Gradle 에서는 일반적으로는 api를 사용하는 것을 권장하지 않지만, 외부모듈에서도 라이브러리를 포함시켜 가져오기 위해서 사용했습니다.
core 모듈은 다른 모듈들에서 사용하는 라이브러리와 같은 역할을 하는 모듈이므로 main 메소드가 존재하지 않기 때문에 bootJar { enabled = false } 설정을 통해 bootJar 옵션을 비활성화 시켜줍니다.
하지만 jar 파일로 빌드되어 외부 모듈에 포함되어야 하기때문에 jar 옵션은 활성화 시켜줍니다.

![image](https://user-images.githubusercontent.com/43958570/196890366-ff5371c3-5bd1-4e8f-aeef-fb7640b403c6.png)


이제 module-core 의 repository 가 잘 동작하는 지 테스트를 해보겠습니다.
```java
package com.blog.application;

import com.blog.domain.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void 임의_회원을_등록한다() {
        memberService.saveAnyMember();
        Member saved = memberService.findAnyMember();
        assertThat(saved.getName(),is("random"));
    }
}
```

> ![image](https://user-images.githubusercontent.com/43958570/196893479-1f36f0c8-5f6b-46e1-83c0-24ed343d7420.png)

위와 같은 에러 메시지와 함께 테스트가 실패합니다. 현재 테스트코드에는 몇가지 문제가 있기 때문입니다.
먼저, MemberService는 SpringFramework에서 제공하는 @Service 어노테이션을 사용하고 있어 MemberService 클래스를 빈으로 등록하였는데, 테스트코드에서는 SpringBoot Context를 불러오지 못하기 때문입니다.
@DataJpaTest 대신 @SpringBootTest 어노테이션을 사용해 spring context를 불러와야 합니다. 
어노테이션을 변경하고 다시 테스트를 실행해도 여전히 동일한 에러가 나타납니다. 
이번에 테스트가 실패한 이유는 module-core 프로젝트는 @SpringBootApplication과 같은 Spring Context를 불러오는 포인트가 없어서 입니다.
실제로는 필요하지 않지만 임시로 사용할 클래스를 만들어 주겠습니다.

### module-core CoreApplicationTests ###
```java
@SpringBootApplication
public class CoreApplicationTests {

    public void contextLoads() {}
}
```

![image](https://user-images.githubusercontent.com/43958570/196895890-1f54f761-b7d2-427a-bafa-b1112446edde.png)

다시 테스트 해보니 이제는 잘 통과하는 걸 볼 수 있습니다.
이제 다음 모듈인 **module-batch** 코드를 작성해보겠습니다.
module-batch 에서는 module-core 의 클래스를 사용할 것이기 때문에 컨트롤러와 서비스를 만들겠습니다.

![image](https://user-images.githubusercontent.com/43958570/196897783-8b526d59-e64d-42f2-9561-13fbdf805d38.png)

### module-batch BatchController ###
```java
@RestController
@RequiredArgsConstructor
public class BatchController {

    private final BatchFacade batchFacade;

    @PostMapping("/")
    public void saveAnyMember() {
        batchFacade.saveAnyMember();
    }

    @GetMapping("/")
    public Member getNewMember() {
        return batchFacade.findAnyMember();
    }
}
```

### module-batch BatchFacade ###
```java
@Service
@RequiredArgsConstructor
public class BatchFacade {

    private final MemberService memberService;

    public void saveAnyMember() {
        memberService.saveAnyMember();
    }

    public Member findAnyMember() {
        return memberService.findAnyMember();
    }
}
```
다음은 module-batch 프로젝트의 build.gradle 에서 사용할 의존성을 추가하겠습니다.
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation project(':module-core')
}
```
batch 모듈은 참조용 모듈이 아니기 때문에 bootJar 옵션을 따로 설정하지 않습니다.
또한, core 모듈의 클래스들에 접근하기 위해 implementation 을 사용하여 core 모듈에 대한 의존성을 명시합니다.

이제 모두 끝났습니다. batch 모듈을 빌드해서 정상적으로 동작하는지 확인만 하면 됩니다.
module-web 프로젝트는 module-batch와 거의 동일한 구조로 되어 있으니 github 코드로 확인하면 좋을 것 같습니다.

### module-batch 실행 ###

![image](https://user-images.githubusercontent.com/43958570/196987801-a89e9bce-c8a3-40ec-a232-4c83843310ef.png)

실행이 잘 된 것 같습니다. 이제 postman을 통해 결과를 확인해보겠습니다.

### module-batch 결과 ###
![image](https://user-images.githubusercontent.com/43958570/196988461-7803e4cf-b4fa-4999-a5db-3c8cbe59e320.png)

post 요청을 보내 임의의 회원을 생성한 후 localhost:8080 에 접속해 보겠습니다.

![image](https://user-images.githubusercontent.com/43958570/196988685-ee127756-336d-4812-8f4e-63b7dbd6f726.png)

등록한 회원 정보가 잘 나타납니다. 
여기까지가 gradle을 사용해서 간단한 multi module 시스템을 구축해보았습니다. 
저도 참고한 글들이 조금 오래된 글들이어서 헤메면서 했습니다. 처음하시는 분들에게 조그만 도움이 되었으면 좋겠습니다.
감사합니다.


### 참고 ###

+ [이동욱님의 블로그](https://jojoldu.tistory.com/m/123)
+ [Backtony님의 블로그](https://backtony.github.io/spring/2022-06-02-spring-module-1/)
+ [Kotlin World](https://kotlinworld.com/317)
+ [Jae Honey 님의 블로그](https://jaehoney.tistory.com/248)










