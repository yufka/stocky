## NatWest Code Challenge

## Report
## Execution time:
Worked on project : 03.11.2022 - 09.11.2022

## General
Implementation and testing was done using Java 11, Spring Boot, Bucket4j, hibernate, H2 in-memory DB, Ehcache Junit5, Yahoo Finance Client, SonarQube, Jacoco, Swagger

## Assumptions
- Stock Ticker is a String name like "AAPL" (example for APPLE company)
- Time Frame is a time point in the future.
- subscription plans do not change much.

## Endpoints
the following resources were implemented:
- POST /user - create new user (user data in body)
- GET /user/subscription (retrieve own subscription plan name)
- PUT /user/subscription (new subscription plan in body)
- POST /stock (Ticker data in body)
<br>
More detailed information available here: https://app.swaggerhub.com/apis/yufka/StockTicker/1.0.0

## Design
### Filters
There are 3 Filters (Authentication, Rate Limiting, Quota Limiting) that run in following order:
- **AuthenticationFilter** checks Basic Authorization headers to make sure that all calls are authorized
- **RateLimitFilter** checks that number of calls are compliant with the subscription plan constraints. For this Bucket4j was used (https://github.com/bucket4j/bucket4j).
- **QuotaLimitFilter** checks only requests that are sent to "/stock" resource and makes sure that all constraints on number of stocks per unit of time are met.

### Database
To save users and subscription plans, I have used the H2 in-memory DB. There are 2 files schema.sql and data.sql that are executed at the beginning.
<br>
A "superuser" is created with the following credentials: <"admin", "admin">, so after application start you can use **curl -u admin:admin**
**Only superuser can create new users**
<br>
Subscription plans are saved in DB and can be added (at the moment only manually). Each subscription plan has a name and settings for (number of calls per time unit) and (number of unique stock ticker per time unit). time units are saved in Milliseconds.

### Caching
In order to reduce the number of calls to DB (real-case scenario), I've decided to use Ehcache to cache users and subscription plans.
- users are cached only for 10 minutes and will be evicted, so that manual deletion of a user from DB does not result in User permanent existence in cache.
- Subscription plans are cached also, thus without lifetime, since I assume that they are long-living objects.

### External Stock API
I have used the **YahooFinanceAPI Client** available as Maven Dependency to access Yahoo Finance (https://github.com/sstrickx/yahoofinance-api).
<br>This Client makes calls to https://query1.finance.yahoo.com/v7/finance/quote?symbols=<ticker_name>
<br>the result JSON is pretty simple and well-structured, so there would not be any problem extracting one property from it, thus I have decided to use the client in the first place

# Bonus
## Improvements
- Management of Quotas (number of unique stock ticker call per time unit) would be the first candidate. The implementation uses a Hashmap that saves called stock tickers with timestamps and in order to "clean" the values I have to iterate over all map entries. I have tried to solve this problem and use the Ehcache (was the first version), thus faced a problem with eviction. I would invest some time to solve this problem first
- When saving Subscriptions in cache I use two caches <Id, Subscription> and <Name, Subscription> to cache both. I would improve this with a more elegant solution where only one cache is used. It does not have any impact on performance or RAM usage (there are not so many subscriptions), thus makes code cleaner.
- Authorization filter seems to be a working solution to check user Authentication, thus Spring Boot Security might be "less manual" way of validating calls' authorization (that would partially replace Authentication filter). Replacement of Filters with Interceptors is also possible.
- I would switch from the automated DB setup defined in application.properties to a solution with DataSource objects - it makes switching to real DBs easier in the future

## Scaling
- **More Instances:** Run few instances (in containers) with some load balancer on the front with real DB, this would also lead to some "shared cache" problematic, so application of something like Hazelcast would probably come up.
- **More stock information providers:** Attaching some other Stock Information providers would definitely make everything more redundant, since dependent API (in my case Yahoo Finance) might have its own rate limits. 
- **More Cache:** In case if application becomes too many requests (Assuming that users request more-or-less the same information at the same time) one might think of caching the results we get from external Stock Information Provider for immediate processing (use case: when stock closes, everybody starts asking for the same information about the same companies)
- **Async:** switch to asynchronous processing (depends how fast the clients want to recieve data)
