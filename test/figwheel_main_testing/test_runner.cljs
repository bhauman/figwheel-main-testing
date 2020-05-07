(ns figwheel-main-testing.test-runner
  (:require
   ;; require all the namespaces that you want to test
   [figwheel-main-testing.reloading-test]
   [figwheel-main-testing.reload-hooks-test]
   [figwheel-main-testing.core-test]
   [cljs-test-display.core]
   [figwheel.main.testing :refer [run-tests-async run-tests]]
   [figwheel.main.system-exit :refer [exit-with-status]]
   [cljs.test :as test :refer [report]]
   [clojure.core.async :refer [<! put! take! go go-loop chan close!] :as asyn]))

(defmethod report [:cljs.test/default :end-run-tests] [{:keys [fail error] :as m}]
  (exit-with-status
   (if (test/successful? m) 0 1)))

(go
  (<! (asyn/timeout 2000))
  (run-tests))
