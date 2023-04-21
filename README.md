# diehard-async

Clojure library to provide safety guard to your application.
Some of the functionality is wrapper over
[Failsafe](https://github.com/jhalterman/failsafe)., is itself a fork of [diehard](https://github.com/sunng87/diehard)

Note that from 0.7 diehard-async uses Clojure 1.9 and spec.alpha for
configuration validation. Clojure 1.8 users could stick with diehard-async
`0.6.0`.

## Usage

A quick example for diehard-async usage.

### Retry block

A retry block will re-execute inner forms when retry criteria matches.

```clojure
(require '[diehard-async.core :as dh])
(dh/with-retry {:retry-on TimeoutException
                :max-retries 3}
  (fetch-data-from-the-moon))
```

### Circuit breaker

A circuit breaker will track the execution of inner block and skip
execution if the open condition triggered.

```clojure
(require '[diehard-async.core :as dh])

(defcircuitbreaker my-cb {:failure-threshold-ratio [8 10]
                          :delay-ms 1000})

(dh/with-circuit-breaker my-cb
  (fetch-data-from-the-moon))

  (dh/with-circuit-breaker {:circuitbreaker my-cb :async :default}
    (fetch-data-from-the-moon-asynchronously))

(dh/with-circuit-breaker
                {:circuitbreaker my-cb
                 :async          :execution}
                (fn [^AsyncExecution execution]
                  (fetch-data-from-the-moon-asynchronously
                           {:on-result         #(.recordResult execution %)
                            :on-exception      #(.recordException execution %)})))
```

### Rate limiter

A rate limiter protects your code block to run limited times per
second. It will block or throw exception depends on your
configuration.

```clojure
(require '[diehard-async.core :as dh])

(defratelimiter my-rl {:rate 100})

(dh/with-rate-limiter my-rl
  (send-people-to-the-moon))
```

### Bulkhead

Bulkhead allows you to limit concurrent execution on a code block.

```clojure
(require '[diehard-async.core :as dh])

;; at most 10 threads can run the code block concurrently
(defbulkhead my-bh {:concurrency 10})

(dh/with-bulkhead my-bh
  (send-people-to-the-moon))
```

### Timeout

Timeouts allow you to fail an execution with `TimeoutExceededException` if it takes too long to complete

```clojure
(require '[diehard-async.core :as dh])

(with-timeout {:timeout-ms 5000}
  (fly-me-to-the-moon))
```

## Examples

### Retry block

```clojure
(dh/with-retry {:retry-on          Exception
                :max-retries       3
                :on-retry          (fn [val ex] (prn "retrying..."))
                :on-failure        (fn [_ _] (prn "failed..."))
                :on-failed-attempt (fn [_ _] (prn "failed attempt"))
                :on-success        (fn [_] (prn "did it! success!"))}
               (throw (ex-info "not good" {:not "good"})))
```

output:

```
"failed attempt"
"retrying..."
"failed attempt"
"retrying..."
"failed attempt"
"retrying..."
"failed attempt"
"failed..."
Execution error (ExceptionInfo) at main.user$eval27430$reify__27441/get (form-init6791465293873302710.clj:7).
not good
```
