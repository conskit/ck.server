(ns ck.server-test
  (:require [ck.server :refer [start-server* register-handler!* server]]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.core :refer [defservice]]
            [puppetlabs.trapperkeeper.services :refer [service-context]]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-cli-data]]
            [org.httpkit.client :as http]
            [ck.server.http-kit])
  (:use midje.sweet))


(defmethod start-server* :test
  [{:keys [handler options]}]
  [handler options])

(fact (start-server* {:provider :test
                      :handler :ring
                      :options true}) => [:ring true])

(def test-container (atom nil))

(with-state-changes
  [(before :facts (do (reset! test-container nil)
                      (register-handler!* test-container :test :ring)))]
  (fact @test-container => {:provider :test
                            :handler :ring}))

(defprotocol ResultService
  (get-result [this]))

(defservice
  test-service ResultService
  [[:CKServer register-handler!]]
  (init [this context]
        (register-handler! :http-kit (constantly {:status 200
                                                  :headers {"Content-Type" "text/plain"}
                                                  :body "Hello World"}))
        context)
  (start [this context]
         {:result (http/get "http://localhost:8080/")})
  (get-result [this]
              (:result (service-context this))))

(with-app-with-cli-data
  app
  [test-service server]
  {:config "./dev-resources/test-config.conf"}
  (let [serv (app/get-service app :ResultService)
        response (get-result serv)]
    (fact (select-keys @response [:status :body]) => {:status 200 :body "Hello World"})))