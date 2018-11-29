# Dang nhap server

ssh root@128.199.205.8

mysql -u root  -p

# ----MYSQL
## install

https://www.fullstackpython.com/blog/install-mysql-ubuntu-1604.html

## Lấy file về và copy vào mysql-files
### tạo file và kéo về



mkdir databases

cd databases

wget https://datasets.imdbws.com/name.basics.tsv.gz

wget https://datasets.imdbws.com/title.akas.tsv.gz

wget https://datasets.imdbws.com/title.basics.tsv.gz

wget https://datasets.imdbws.com/title.crew.tsv.gz

wget https://datasets.imdbws.com/title.episode.tsv.gz

wget https://datasets.imdbws.com/title.principals.tsv.gz

wget https://datasets.imdbws.com/title.ratings.tsv.gz

 

gunzip \*.gz

cp -a /root/databases/. /var/lib/mysql-files/

source /root/file/import.txt
chmod 0777 /var/lib/mysql-files
source /root/file/convert.txt



# ----ORIENTDB

https://websiteforstudents.com/install-and-configure-orientdb-on-ubuntu-16-04-18-04-lts-servers/

## copy file json kieu "document" tai ve vao /opt/orientdb/lib/orientdb

cp -a /root/file/json/. /opt/orientdb/lib/

## copy file json kieu "graph"

cp -a /root/file/graph/. /opt/orientdb/lib/

cd ../opt/orientdb/lib/

## Chay lenh de import 

cd ../opt/orientdb/bin/

./oetl.sh /opt/orientdb/lib/name_basics.json 

./oetl.sh /opt/orientdb/lib/title_akas.json 

./oetl.sh /opt/orientdb/lib/title_basics.json 

./oetl.sh /opt/orientdb/lib/title_crew.json 

./oetl.sh /opt/orientdb/lib/title_episode.json 

./oetl.sh /opt/orientdb/lib/title_principals.json 

./oetl.sh /opt/orientdb/lib/title_ratings.json 
# index
https://github.com/orientechnologies/orientdb/issues/6415

CREATE PROPERTY NameBasics.birthYear INTEGER

CREATE INDEX NameBasics.birthYear ON NameBasics (birthYear) NOTUNIQUE METADATA {ignoreNullValues: false}


## Vao thu muc run 

cd ../opt/orientdb/bin/

./console.sh

CONNECT plocal:/opt/orientdb/databases/orientdb admin admin 

LIST CLASSES

select from Account

# CHECK + lỗi "rw"
ps aux | grep "orientdb" | grep -v grep | awk '{print $2}'

=> K connect database đó thì sẽ dùng đc
# CREATE database in opt

CREATE DATABASE PLOCAL:/opt/orientdb/databases/orientdb root 123456
CONNECT PLOCAL:/opt/orientdb/databases/orientdb admin admin 
CONNECT PLOCAL:/opt/orientdb/databases/orientdb orientdb 






