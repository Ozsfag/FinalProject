# **����������, ����������� ������������� �������� � ������������ �� ��� ������� �����.**
<!-- TOC -->
* ������ ���������������� �� ��������� Spring;
* ���������� ������� ��� �������� ����� ������� � �������;
* ����������� �������� ����� � ����������� ����� �� ��������� ����� � �� ���������� ������� ���������� �������� ����������� (��������������� ���������� �������) ��������;
* ����������� ������� ������ ���������� � �������������� ���������� ���������� �������.

# **��� ������ ���������� ����������, ���������� �� ��������� MySQL ��� �������� ������ ������������������ ������.** 
� application.yml ������� ���� ������� ������ � ������ ������ ��� ����������, �������� :
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
                      name: �����.��
                    - url: https://skillbox.ru/
                      name: Skillbox
                    - url: http://www.playback.ru/
                      name: PlayBack.Ru
<!-- TOC -->

��������� ����������. � �������� ������ ������� http://localhost:8080/.