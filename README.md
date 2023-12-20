## **����������, ����������� ������������� web-�������� � ������������ �� ��� ������� �����.**
<!-- TOC -->
* ������ ��������������� �� ���������� Spring;
* � ������������� ������ ������� ��� �������� ����� ������� � �������;
* ����������� �������� ����� � ����������� ����� �� ��������� ����� � �� ���������� ������� ���������� �������� ����������� (��������������� ���������� �������) ��������;
* ����������� ������� ������ ���������� � �������������� ���������� ���������� �������.

### **��� ������ ���������� ����������, ���������� �� ��������� _MySQL_ ��� �������� ������ ������������������ ������.** 
-[ ] � application.yml ������� ���� ������� ������ � ������ ������ ��� ����������, �������� :
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
                      name: �����.��
                    - url: https://skillbox.ru/
                      name: Skillbox
                    - url: http://www.playback.ru/
                      name: PlayBack.Ru
            connection-settings:
                user-agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0
                referrer: https://www.google.com
<!-- TOC -->

-[ ] ��������� ����������. � �������� ������ ������� http://localhost:8080/.