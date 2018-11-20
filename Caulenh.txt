# Dang nhap server

ssh root@178.128.58.224

mysql -u root  -p



# copy file tai ve vao /opt/orientdb/lib/orientdb

cp -a /root/file/json/. /opt/orientdb/lib/

cd ../opt/orientdb/lib/

# Chay lenh de import 

cd ../opt/orientdb/bin/

./oetl.sh /opt/orientdb/lib/name_basics.json 
./oetl.sh /opt/orientdb/lib/title_akas.json 
./oetl.sh /opt/orientdb/lib/title_basics.json 
./oetl.sh /opt/orientdb/lib/title_crew.json 
./oetl.sh /opt/orientdb/lib/title_episode.json 
./oetl.sh /opt/orientdb/lib/title_principals.json 
./oetl.sh /opt/orientdb/lib/title_ratings.json 

# Vao thu muc run 

cd ../opt/orientdb/bin/

./console.sh

CONNECT plocal:/usr/local/orientdb/databases/orientdb admin admin 

LIST CLASSES

select from Account

# 





