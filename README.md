# **ѕриложение, позвол€ющее индексировать страницы и осуществл€ть по ним быстрый поиск.**
<!-- TOC -->
* движок разрабатываетьс€ на фрейворке Spring;
* рекурсивно обходит все страницы сайта начина€ с главной;
* индексирует страницы сайта Ч подсчитыва€ слова на страницах сайта и по поисковому запросу определ€ть наиболее релевантные (соответствующие поисковому запросу) страницы;
* реализована система поиска информации с использованием созданного поискового индекса.

# **ƒл€ работы приложени€ необходимо, установить на компьютер MySQL дл€ хранени€ данных проиндексированных сайтов.** 
¬ application.yml указать ваши учетные данные и список сайтов дл€ индексации, например :
 <!-- TOC -->
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
            ndexing-settings:
                sites:
                    - url: https://lenta.ru/
                      name: Ћента.ру
                    - url: https://skillbox.ru/
                      name: Skillbox
                    - url: http://www.playback.ru/
                      name: PlayBack.Ru
<!-- TOC -->

«апустите приложение. ¬ адресную строку введите http://localhost:8080/.