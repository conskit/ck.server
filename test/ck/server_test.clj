(ns ck.server-test
  (:require [ck.server :refer [start-server* register-handler!* server]]
            [ck.routing :refer [router]]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.core :refer [defservice]]
            [puppetlabs.trapperkeeper.services :refer [service-context]]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-cli-data]]
            [org.httpkit.client :as http]
            [conskit.macros :refer [defcontroller action]]
            [conskit.core :as ck]
            [ck.server.http-kit]
            [ck.routing.bidi])
  (:use midje.sweet))


(defmethod start-server* :test-server
  [{:keys [handler options]}]
  [handler options])

(fact (start-server* {:provider :test-server
                      :handler :ring
                      :options true}) => [:ring true])

(def test-container (atom nil))

(with-state-changes
  [(before :facts (do (reset! test-container nil)
                      (register-handler!* test-container :test-server :test-handler)))]
  (fact @test-container => {:provider :test-server
                            :handler :test-handler}))

(defprotocol ResultService
  (get-result [this]))

(defcontroller
  test-ctrlr
  []
  (action ^{:route "/"} hello-world [req] {:status 200 :headers {"Content-Type" "text/plain"} :body "Hello Conskit"}))

(defservice
  test-service ResultService
  [[:CKServer register-handler!]
   [:ActionRegistry register-controllers!]]
  (init [this context]
        (register-controllers! [test-ctrlr])
        (register-handler! :http-kit :bidi)
        context)
  (start [this context]
         {:result (http/get "http://localhost:8080/")})
  (get-result [this]
              (:result (service-context this))))

(with-app-with-cli-data
  app
  [ck/registry router test-service server]
  {:config "./dev-resources/test-config.conf"}
  (let [serv (app/get-service app :ResultService)
        response (get-result serv)]
    (fact (select-keys @response [:status :body]) => {:status 200 :body "Hello Conskit"})))