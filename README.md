# blockblastproje

JAVAFX KURULUMU

https://gluonhq.com/products/javafx/ adresinden işletim sistemine uygun JavaFX SDK'yı indir.

ZIP dosyasını açıp C:\javafx-sdk-XX gibi kolay bir yere çıkar.

JavaFX’i Classpath’e Ekle:

Projeye sağ tıkla → Build Path > Configure Build Path

Libraries > Add External JARs → javafx-sdk-21/lib klasöründeki tüm .jar dosyaları ekle.

Run > Run Configurations > Arguments > VM Arguments bölümüne şunu yaz: 

     --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml


KODUN ÇALIŞMASI İÇİN MYSQL BAĞLANTISI:

1 MySQL'i indirip kurun https://dev.mysql.com/downloads/mysql/

2 MySQL'e giriş yapın: mysql -u root -p

3 blockblast veritabanını oluşturun:

    CREATE DATABASE blockblast;
                              
    USE blockblast;
4 Tabloyu oluşturun: 

                CREATE TABLE IF NOT EXISTS game_results (  
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(100) NOT NULL,
                    score INT NOT NULL,
                    played_at DATETIME NOT NULL
                );
                
Java Projesine MySQL JDBC Bağlantı Sürücüsünü Ekleyin: https://dev.mysql.com/downloads/connector/j/  jar dosyasını indirip projenize ekleyin.Configure build path,libraries,classpath,add external jars.


SON OLARAK PROJEDE MYSQL PASSWORD KISMINI KENDİ OLUŞTURDUĞUNUZ ŞİFRENİNİZİ GİRİN
