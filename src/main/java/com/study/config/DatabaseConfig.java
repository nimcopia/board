package com.study.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration //스프링 레거시의 xml 설정 방식을 스프링 부트에서는 Java 클래스로 대체한다
@PropertySource("classpath:/application.properties") //이 클래스가 참조할 properties의 경로를 지정함, 즉 @ConfigurationProperties의 경로
public class DatabaseConfig {

    @Autowired //빈으로 등록된 객체를 클래스에 주입함 @Resource, @Inject 등이 존재함 -> 롬복 라이브러리를 이용해서 스프링에서 권장하는 생성자 주입 방식을 이용
    private ApplicationContext context; //ApplicationContext는 스프링 컨테이너중 하나임 빈의 생성과 사용, 관계, 생명 주기 등을 관리함

    @Bean
    /*
    빈은 쉽게 말해 자바의 객체임, 프로젝트에 100개의 클래스가 있다고 가정하면 클래스들이 서로에 대한 의존성이 높을 때 결합도가 높다고 표현하는데
    이러한 문제를 컨테이너에서 빈을 주입받는 방법으로 해결함. 즉 클래스간의 의존성을 낮출 수 있음
    빈은 Configuration 클래스의 메서드 래밸에만 선언이 가능하고 빈이 선언된 객체는 스프링 컨테이너에 의해 관리되는 빈으로 등록됨
    */
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    /*
    이 어노테이션은  prefix를 지정할 수 있는데 @PropertySource(application.properties)에서 선언된 파일에 해당하는 접두사로
    시작하는 설정을 모두 읽어 들여 해당 메서드에 매핑함.
    이 어노테이션은 메소드 뿐만 아니라 클래스 래밸에도 선언이 가능함
     */
    public HikariConfig hikariConfig() {return new HikariConfig();}

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        //SqlSessionFactory 객체를 생성함, 이것은 DB커넥션과 sql 실행에 대한 모든 것을 가짐
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        /*
        SqlSessionFactoryBean은 FactoryBean 인터페이스의 구현 클래스로, 마이바티스와 스프링 모듈로 사용됨
        factoryBean 객체는 데이터 소스를 참조하고 XML Mapper(sql 쿼리 작성 파일)의 경로와 설정 파일 경로 등의 정보를 갖는 객체임
        */
        factoryBean.setDataSource(dataSource());
        factoryBean.setMapperLocations(context.getResources("classpath:/mappers/**/*Mapper.xml"));
        /*
        xml 매퍼 경로임 스프링 부트가 context.getResource()에 선언한 경로에서 xml Mapper를 찾아 읽음.
        classpath = src/main/resource 즉, 전체 경로는 src/main/resource/mappers/(폴더명/{파일명}Mapper.xml
        */
        factoryBean.setConfiguration(mybatisConfig()); //mybatisConfig 메소드(밑밑 메소드)의 빈을 이용해서 MyBatis 옵션을 설정함
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSession() throws Exception {
        /*
        sqlSession 객체를 생성함. SqlSessionTemplate는 마이바티스 스프링 연동 모듈의 핵심으로 sqlSession을 구현하고, 세션의 생명주기를 관리함
        SqlSessionTemplate은 SqlSessionFactory를 통해 생성되고 db의 커밋, 롤백 등 sql의 실행에 필요한 모든 메서드를 가짐
        */
        return new SqlSessionTemplate(sqlSessionFactory());
    }


    @Bean
    @ConfigurationProperties(prefix = "mybatis.configuration")
    //application.properties에서 prefix에 해당하는 문장으로 시작하는 모든 설정 읽어 스프링 컨테이너에 빈으로 등록, 이 프로젝트는 카멜케이스 불러오려는 용도
    public org.apache.ibatis.session.Configuration mybatisConfig() {
        return new org.apache.ibatis.session.Configuration();
    }

}