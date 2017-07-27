# Ferma OrientDB Vert.x Extension

Similar to ```Vertx.executeBlocking``` the method ```asyncTx``` can be used in combination with Vert.x. 
The given handler will be executed within a transaction and a dedicated worker pool thread. 
Please note that the transaction handler may be executed multiple times in order to retry the transaction code when an OConcurrentModificationException occurred. 

```java
  Future<Person> future = Future.future();
  graph.asyncTx(tx -> {
    Person p = tx.getGraph().addFramedVertex(Person.class);
    tx.complete(p);
  }, (AsyncResult<Person> rh) -> {
    future.complete(rh);
  });
```
