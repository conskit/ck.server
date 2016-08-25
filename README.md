# ck.server [![Build Status](https://travis-ci.org/conskit/ck.server.svg?branch=master)](https://travis-ci.org/conskit/ck.server) [![Dependencies Status](https://jarkeeper.com/conskit/ck.server/status.svg)](https://jarkeeper.com/conskit/ck.server) [![Clojars Project](https://img.shields.io/clojars/v/ck.server.svg)](https://clojars.org/ck.server)


Web Server Module for [conskit](https://github.com/conskit/conskit).

## Installation
Add the dependency in the clojars badge above in your `project.clj`.

### http-kit support
Add the classifier "http-kit" and the `http-kit` library.

## Usage

Add the following to your `bootstrap.cfg`:

```
ck.sever/server
```

Add the following to your `config.conf`

```properties
server: {
  port: 8080
}
```

Add the dependency in your serivice and call the `register-handler!` method in the init phase.

```clojure
(defservice
  my-service
  [[:CKServer register-hanlder!]]
  (init [this context]
    ...
    (register-handler! :http-kit :bidi)
  ...)
```

`register-handler!` is called with a web server provider (`:http-kit`) and a provider for routing (`:bidi`). ck.server depends on a service being present that implements `:CKRouter` and provides a `make-ring-handler` method ([ck.routing](https://github.com/conskit/ck.routing) provides this). 

When your service is initialized it will register the both providers and then once the web server service starts it will make a ring handler and start the server with it

### Alternatives
If Http-Kit is not your cup of tea you can always implement your own provider by providing a method that extends the `ck.server/start-server*` multimethod

```clojure
(defmethod start-server* :my-special-provider
  [{:keys [handler options]}]
  ;; logic
  )
  
;; Within service
(register-handler! :my-special-provider :bidi)
```

where `handler` is a ring handler and `options` is the configuration settings specified in `config.conf` (see `http-kit/ck/server/http_kit.clj` for an example).

## License

Copyright © 2016 Jason Murphy

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
