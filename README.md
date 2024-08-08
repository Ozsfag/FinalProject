## **Приложение, позволяющее индексировать web-страницы и осуществлять по ним быстрый поиск.**

<!-- TOC -->

* движок разрабатывается на фреймворке Spring;
* в многопоточном режиме обходит все страницы сайта начиная с главной;
* индексирует страницы сайта — подсчитывая слова на страницах сайта и по поисковому запросу определять наиболее
  релевантные (соответствующие поисковому запросу) страницы;
* реализована система поиска информации с использованием созданного поискового индекса.

### **Для работы приложения необходимо, установить на компьютер _Postgres_ для хранения данных проиндексированных
сайтов.**

-[ ] В application.yml указать ваши учетные данные и список сайтов для индексации, например :

 <!-- TOC -->

            server:
                port: 8080
                max-http-header-size: 20000

            spring:
                datasource:
                    driver-class-name: org.postgresql.Driver
                    password: 30091998As!
                    url: jdbc:postgresql://localhost:5432/search_engine
                    username: postgres
                    type: com.zaxxer.hikari.HikariDataSource
                hikari:
                    minimum-idle: 2
                    idle-timeout: 600000
                    maximum-pool-size: 10
                    auto-commit: true
                    pool-name: HikariCorePool
                    max-lifetime: 1800000
                    connection-timeout: 30000

            jpa:
                properties:
                    hibernate:
                        dialect: org.hibernate.dialect.PostgreSQLDialect
                hibernate:
                    ddl-auto: update
                show-sql: true

            indexing-settings:
                sites:
                    - url: https://lenta.ru/
                      name: Лента.ру
                    - url: https://skillbox.ru/
                      name: Skillbox
                    - url: http://www.playback.ru/
                      name: PlayBack.Ru

            connection-settings:
                user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36
                referrer: https://www.google.com

            morphology-settings:
                russianParticleNames:
                    - МЕЖД
                    - ПРЕДЛ
                    - СОЮЗ
                englishParticlesNames:
                    - CONJ
                    - PREP
                    - ARTICLE
                    - INT
                    - PART
                notCyrillicLetters: "[^а-я]"
                notLatinLetters: "[^a-z]"
                splitter: "\\s+"
                emptyString: " "
                formats:
                    - .pdf
                    - .jpg
                    - .docx
                    - .doc
                    - .JPG
                    - .jpeg
                    - "#"

<!-- TOC -->

-[ ] Запустите приложение. В адресную строку введите http://localhost:8080/.