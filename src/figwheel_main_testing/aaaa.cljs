(ns ^:figwheel-no-load figwheel-main-testing.aaaa
  (:require [figwheel-main-testing.bbbb]
            [figwheel-main-testing.util :as util]))

(util/add-ns 'figwheel-main-testing.aaaa)

(defn multiply [a b] (* a b))

(defn ^:after-load on-reload []
  (util/reload-hook-ns 'figwheel-main-testing.aaaa)    
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
