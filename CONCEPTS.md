## Kafka main concepts:

**Kafka**: A messaging system like RabbitMQ and ActiveMQ.

**Broker**: A server that participates in a Kafka cluster. There can be multiple brokers.

**Topic**: Logical storage of ordered messages. Each topic has a unique name that consumers and producers use. A SQL equivalent would be a Table.

**Partition**: Each topic can have multiple partitions. All consumers of a topic don't read from all partitions, instead they're dynamically assigned topics. Partition is uniquely assigned to consumers within a consumer group. Partitions are the units that are replicated in the cluster to protect against data loss.

**Message**: A record in a partition. It's the data that a producer publishes and a consumer consumes. SQL equivalent would be a row in a table.

**Message Offset**: Message index in a partition. It starts from zero and keeps on increasing without reuse.

**Producer**: Application that publishes message to a topic. Producer assigns a key to the message (generally a UUID) that is used by Kafka to distribute the message among the partitions of topic. Key should be selected to ensure even distribution of messages among partitions.

**Consumer**: Consumers (Kafka client) poll for messages from a Kafka topic. They're assigned set of partitions dynamically or they can also ask to poll specific partitions. Consumers are part of a consumer group.

**Consumer Group**: A named set of consumers. Consumers within a group divide partitions from the topic they listen to among themselves. Partitions are divided again if a consumer joins or leaves the group.

**Consumer Group Offset**: Partitions store offset of last message read by each group. This is used by Kafka to keep track of read and unread messages by a group. Only unread messages are returned by Kafka in order when polled by a consumer in a group. It can be reset manually by running Kafka commands.



**spring kafka** - support traditional blocking springboot stack (servlet based)

**reactive kafka** - complete reactive based , async stack end to end can be achieved,need to be used with Spring Webflux for better performance


### If you use spring-kafka with Spring webflux instd of reactive-kafka
**Performance** -	Can block if not handled correctly |	Fully non-blocking, better scalability
Spring Kafka is imperative (blocking) by default, while Spring WebFlux is reactive.
Mixing blocking & non-blocking can cause performance issues if not handled properly.


// 
So for high-performance,high throughput, event-driven app use reactive-kafka with spring webflux


This combination of Reactive Kafka Streams and Spring WebFlux offers a robust solution for building applications that require distributed, real-time, and reactive data pipelines.

----------------------------------------------------------------------------------------------------------------------------------
_**High throughput**_ refers to the ability of a system to process a large number of requests or messages per second (RPS or TPS - transactions per second).

 Example:
A Kafka consumer processing 1 million messages per second = High throughput.

**_Low latency_** means minimizing the time delay between sending a request and receiving a response (measured in milliseconds or microseconds).

 Example:
A stock trading system processing transactions in 1 millisecond = Low latency.
A Kafka consumer receiving a message in 5ms after it was produced = Low latency.

---------------------------------------------------------------------------------------------------------------------------------
Kafka Consumer **Polling** Behavior
Kafka consumers poll topics periodically to retrieve messages. The polling mechanism ensures efficient message consumption while balancing load across multiple consumers in a group.

**How Polling Works**
A consumer polls Kafka at regular intervals to get new messages.
It processes the messages and then polls again.
If a consumer stops polling, Kafka assumes it has failed and reassigns partitions to other consumers.

Key Polling Configurations

###### max.poll.records (Default: 500)
Defines the maximum number of records a consumer can fetch in a single poll.

###### max.poll.interval.ms (Default: 300000ms or 5 minutes)
Defines the maximum time allowed for message processing between two consecutive poll() calls.
If a consumer takes longer than this time to process messages, Kafka assumes it has crashed and revokes its partitions.

>What is Backpressure in Reactive Programming?
	Backpressure is a flow control mechanism used in Reactive Programming to prevent fast data producers from overwhelming slow consumers.

_In simple terms_:
If a producer generates data faster than the consumer can handle, backpressure helps the consumer control the flow instead of crashing due to overload.
It ensures stability, efficiency, and resource optimization in reactive systems.

>Use Backpressure when:
*  Processing Kafka messages in a non-blocking way (Reactive Kafka).
*  Handling large real-time event streams (Stock trading, IoT data, logs).
*  Calling external APIs in WebFlux that respond slower than expected.


Spring WebFlux internally uses?Project Reactor?and its publisher implementations,?Flux?and?Mono.

It supports two programming models:
?Annotation-based reactive components
?Functional routing and handling

**Reactor Kafka** is a reactive API for?Apache Kafka?based on?Project Reactor. Reactor Kafka API enables messages to be published to Kafka topics and consumed from Kafka topics using functional APIs with non-blocking back-pressure and very low overheads. 
This enables applications using Reactor to use Kafka as a message bus or streaming platform and integrate with other systems to provide an end-to-end reactive pipeline.

###### Why Is supplyAsync() Considered Asynchronous? CompletableFuture OR traditional Async API Reality

When you call:

	CompletableFuture.supplyAsync(() -> {
		System.out.println("Running in thread: " + Thread.currentThread().getName());
		return "Hello";
	});

The function inside supplyAsync() runs in a separate thread from the caller. - BUT NOT NON-BLOCKING
The caller does NOT wait for the task to complete (hence, it's "_asynchronous_").
However, this does not mean non-blocking execution it simply offloads work to another thread.

**Async vs. Non-Blocking**

✅  CompletableFuture is Asynchronous 
* It doesn't block the caller. The task runs on a different thread.
* The main thread can do other work while the computation runs in the background.

❌ But It's Not Fully Non-Blocking
* If the logic inside supplyAsync() contains a blocking call (like an API request or DB query), the assigned thread gets blocked.
* Since Java's ForkJoinPool uses a limited thread pool, too many blocking calls can exhaust the pool and slow down execution.

WebFlux and Reactor(Mono,Flux) provide true non-blocking execution, avoiding thread exhaustion.
If you need high scalability for I/O-bound tasks (like API calls), prefer WebClient over CompletableFuture.

That's why using Spring-Kafka with Executor service + Completable future (Multithreaded Async Approach) is not suitable for calling an external API (It can be slow)
Better approach is to use Spring-Kafka with Webclient , which will not block thread, Complete event driven and Non-Blocking

**Spring Kafka with WebFlux**: Will WebClient Be Non-Blocking or Blocking?

Spring Kafka does NOT run on Tomcat because Kafka listeners (@KafkaListener) run in a separate thread pool, not in the Tomcat request-handling threads. This means WebClient will still be non-blocking even if you use @KafkaListener inside a Spring Boot (Tomcat) application.

**How It Works**
1. Spring Kafka (@KafkaListener) runs in a separate thread pool (not Tomcat).
2. WebClient is inherently non-blocking, so it does not block the Kafka consumer thread.
3. Even if Spring Boot runs on Tomcat, that only affects HTTP requests not Kafka consumers.



Good to know
How many api calls CCM can accept at a time ( we can set flatMap - Limit concurrency (flatMap(, 10))	Controls parallel API calls)
Response time of CCM api
	If response time is more, polling config needs to be tuned, 
What is the avg volume of messages produced in Kafka
	If kafka produces more then backpressure needs to be setup in consumer

**3 approaches**

1. Spring Kafka (KafkaListener) + Rest Template (With Multithreaded Async) - Blocking, not efficient 
2. Spring Kafka + Spring WebFlux - Good Enough, won't Block Kafka 
3. Reactive Kafka + Spring WebFlux - Complete Non- Blocking




Spring Kafka with Virtual Threads **vs** Reactive Kafka with WebFlux

Requirement- create a kafka consumer which recieves 5000-10000 msgs at a time 4-5 times a day. for each msg need to validate, store in postgres db and call an external api and store its response in db.

Comparison of Both Approaches:

| Criteria	| Spring Kafka with Virtual Threads	| Reactive Kafka with WebFlux |
| ------------- | ------------- | ----- |
| Threading | Model	Virtual threads allow efficient thread management	| Fully non-blocking, uses Reactors backpressure |
| Concurrency	| Handles concurrency using threads or virtual threads	| Handles concurrency with reactive streams (Flux/Mono) |
| Backpressure	| Manual management needed for backpressure	| Built-in backpressure with reactive streams |
| Database Interaction	| Can be blocking (traditional RDBMS driver)	| Non-blocking (using R2DBC) |
| External API Calls	| Synchronous or async with threads, may block waiting for API	| Non-blocking, using WebClient |
| Scalability	| Can scale with threads, but may be limited by thread pool size	| Scales naturally with reactive streams |
| Code Complexity	| Simpler if you're familiar with async programming	| More complex due to the reactive paradigm |
| System Load	| Efficient with virtual threads but still thread-based	| Very efficient with minimal thread usage |

**Recommendation Based on Your Requirements:**

**Message Volume:** Since you are processing 5000-10,000 messages at a time, Reactive Kafka with WebFlux might be a better fit because it can naturally scale with high concurrency, especially in scenarios with a lot of I/O-bound tasks (like external API calls and database inserts). The non-blocking nature of reactive systems helps ensure that the system can handle high loads efficiently without thread contention.


**I/O Bound Operations:** Since your requirements involve database storage and external API calls, which are I/O-bound operations, Reactive Kafka with WebFlux is ideal as it maximizes system throughput by not blocking threads waiting for I/O.


**Backpressure and Flow Control:** Reactive Kafka handles backpressure naturally, which is important when you're processing large batches of messages. If the external API is slow, or if the database is under load, reactive systems can adjust the flow of messages.


**Development Team:** If your team is familiar with reactive programming and Project Reactor, then Reactive Kafka with WebFlux is a great choice. If the team is more comfortable with the traditional Spring async model, then Spring Kafka with virtual threads might be quicker to implement.

**Conclusion:**
For processing large batches of messages (5000-10,000 at a time), Reactive Kafka with WebFlux is likely the more scalable and efficient option, especially given the heavy I/O-bound nature of your workload (database interactions and API calls). However, if you're more familiar with traditional async programming and virtual threads, Spring Kafka with virtual threads can still be a solid choice, but it requires more manual management of backpressure and concurrency.

Implementing a circuit breaker is must, what is external system is down, backpressure handle is not enough

https://medium.com/@jhuliana.gonzalez/spring-webflux-optimization-metrics-circuit-breaker-and-resilience-patterns-for-microservices-fb7470ad177c