(ns ck.server.http-kit
  (:require [ck.server :refer [start-server*]]
            [org.httpkit.server :refer [run-server]]))

(defmethod start-server* :http-kit
  [{:keys [handler options]}]
  (run-server handler options))
