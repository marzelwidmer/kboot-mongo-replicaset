# ReplicaSet
(https://flowygo.com/en/blog/mongodb-and-docker-how-to-create-and-configure-a-replica-set/)

```bash 
docker-compose up
```

```bash 
docker-compose exec mongodb1 mongo
```


```bash
rsconf = {
   _id : "rsmongo",
   members: [
       {
           "_id": 0,
           "host": "mongodb1:27017",
           "priority": 4
       },
       {
           "_id": 1,
           "host": "mongodb2:27017",
           "priority": 2
       },
       {
           "_id": 2,
           "host": "mongodb3:27017",
           "priority": 1
       }
   ]
}
 
```

```bash
rs.initiate(rsconf);
```

```bash
rs.conf() 
```
