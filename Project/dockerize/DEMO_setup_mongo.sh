#!/bin/sh
docker cp ../DEMO_millepedibus_db/. mongo:/millepedibus_db

docker exec -it mongo sh -c " mongorestore --db millepedibus millepedibus_db "