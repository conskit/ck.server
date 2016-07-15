(ns ck.server
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [puppetlabs.trapperkeeper.services :refer [service-context]]
            [clojure.tools.logging :as log]))

(def ^:private container (atom nil))

(defn register-handler!* [container provider handler]
  (reset! container {:provider provider
                     :handler  handler}))

(defmulti start-server* :provider)

(defprotocol CKServer
  "Server Functions"
  (register-handler! [this provider handler]))


(defservice
  server CKServer
  [[:ConfigService get-in-config]]
  (start [this context]
         (let [options (get-in-config [:server])]
           (log/info (str "Starting Web server on port " (:port options)))
           (assoc context :stopper (start-server* (merge @container {:options options})))))
  (stop [this context]
        (log/info "Stopping Web server")
        (let [stop (get context :stopper)]
          (stop))
        context)
  (register-handler! [this provider handler]
                     (register-handler!* container provider handler)))
