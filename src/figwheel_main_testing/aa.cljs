(ns figwheel-main-testing.aa
  (:require [figwheel-main-testing.g-some]
            [figwheel-main-testing.util :as util]))

(util/add-ns 'figwheel-main-testing.aa)

(defn multiply [a b] (* a b))

(defn ^:after-load on-reload []
    (util/reload-hook-ns 'figwheel-main-testing.aa)  
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
