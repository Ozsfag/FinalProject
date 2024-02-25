## **Приложение, позволяющее индексировать web-страницы и осуществлять по ним быстрый поиск.**
<!-- TOC -->
* движок разрабатывается на фреймворке Spring;
* в многопоточном режиме обходит все страницы сайта начиная с главной;
* индексирует страницы сайта — подсчитывая слова на страницах сайта и по поисковому запросу определять наиболее релевантные (соответствующие поисковому запросу) страницы;
* реализована система поиска информации с использованием созданного поискового индекса.

### **Для работы приложения необходимо, установить на компьютер _MySQL_ для хранения данных проиндексированных сайтов.** 
-[ ] В application.yml указать ваши учетные данные и список сайтов для индексации, например :
 <!-- TOC -->
            server:
                port: 8080
            spring:
                datasource:
                    driverClassName: com.mysql.cj.jdbc.Driver
                    username: name
                    password: pass
                    url: jdbc:mysql://localhost:3306/search_engine?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true
                jpa:
                    properties:
                        hibernate:
                            dialect: org.hibernate.dialect.MySQL8Dialect
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
                user-agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0
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
<!-- TOC -->

-[ ] Запустите приложение. В адресную строку введите http://localhost:8080/.
