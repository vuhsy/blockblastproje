# blockblastproje
YAPILACAKLAR:

1-MySql ile skor ve high skor sistemi

2-Giriş ve bitme ekranları oyun bittiğinde yeniden oyna butonu  en iyi skor ve güncel skorun yazması lazım

3-Blok patlama animasyonları,ses efektleri ve combo sistemi.


KODUN ÇALIŞMASI İÇİN MYSQL BAĞLANTISI:

1 MySQL'i indirip kurun https://dev.mysql.com/downloads/mysql/

2 MySQL'e giriş yapın: mysql -u root -p

3 mydb veritabanını oluşturun:

    CREATE DATABASE mydb;
                              
    USE mydb;
4 Tabloyu oluşturun: 

                CREATE TABLE IF NOT EXISTS game_results (  
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(100) NOT NULL,
                    score INT NOT NULL,
                    played_at DATETIME NOT NULL
                );
                
Java Projesine MySQL JDBC Bağlantı Sürücüsünü Ekleyin: https://dev.mysql.com/downloads/connector/j/  jar dosyasını indirip projenize ekleyin.Configure build path,libraries,classpath,add external jars.


SON OLARAK PROJEDE MYSQL PASSWORD KISMINI KENDİ OLUŞTURDUĞUNUZ ŞİFRENİNİZİ GİRİN
