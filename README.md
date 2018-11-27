# Dang nhap server

ssh root@178.128.58.224

mysql -u root  -p

# ----MYSQL
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

source /root/file/import.txt
source /root/file/convert.txt

cp -a /root/databases/. /var/lib/mysql-files/

# ----ORIENTDB

## copy file js tai ve vao /opt/orientdb/lib/orientdb

cp -a /root/file/json/. /opt/orientdb/lib/

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






