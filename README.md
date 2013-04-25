====================
 Description:
====================

A prototype application to demonstrate use of GridFS for binary content storage. The prototype app provides implementation for Create, Get and List operations using PostgreSQL, Oracle and GridFS.


====================
 Running App:
====================

mvn clean jetty:run


====================
 Replication Setup:
====================

MONGO_HOME=/Users/Aparna/Development/Projects/NoSQL/MongoDB

$MONGO_HOME/mongodb/bin/mongod --replSet nosqlmatters --dbpath $MONGO_HOME/mongodata/replica1 --port 27017 --rest --directoryperdb --fork --logpath $MONGO_HOME/log/replica1.log

$MONGO_HOME/mongodb/bin/mongod --replSet nosqlmatters --dbpath $MONGO_HOME/mongodata/replica2 --port 27018 --rest --directoryperdb --fork --logpath $MONGO_HOME/log/replica2.log

$MONGO_HOME/mongodb/bin/mongod --replSet nosqlmatters --dbpath $MONGO_HOME/mongodata/replica3 --port 27019 --rest --directoryperdb --fork --logpath $MONGO_HOME/log/replica3.log

mongo --port 27017

rsconf = {
           _id: "nosqlmatters",
           members: [
                      {
                       _id: 0,
                       host: "server.local:27017"
                      }
                    ]
         }
         
         
rs.initiate( rsconf )

rs.conf()

rs.add("server.local:27018")
rs.add("server.local:27019")
