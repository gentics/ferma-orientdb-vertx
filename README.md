# Ferma Extension for OrientDB

This extension provides various wrappers and abstract classes that are very useful when using OrientDB with Ferma.


```java
  OrientGraphFactory graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
  OrientDBTrxFactory graph = new OrientDBTrxFactory(graphFactory, Vertx.vertx());
  
  try (Trx tx = graph.trx()) {
     Person p = tx.getGraph().addFramedVertex(Person.class);
     tx.success();
  }
```

## Vert.x Integration

Similar to ```Vertx.executeBlocking``` the methods ```asyncNoTrx``` and ```asyncTrx`` can be used in combination with vertx. 
The given handler will be executed within a transaction and a dedicated worker pool thread. 
Please note that the transaction handler may be executed multiple times in order to retry the transaction code when an OConcurrentModificationException occurred. 

```java
  Future<Person> future = Future.future();
  graph.asyncNoTrx(noTrx -> {
    Person p = tx.getGraph().addFramedVertex(Person.class);
    noTrx.complete(p);
  }, (AsyncResult<Person> rh) -> {
    future.complete(rh);
  });
```
