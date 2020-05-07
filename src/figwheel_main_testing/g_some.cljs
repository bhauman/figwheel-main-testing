(ns ^:figwheel-hooks figwheel-main-testing.g-some
  (:require
   [figwheel-main-testing.util :as util]))

(util/add-ns 'figwheel-main-testing.g-some)

(defn multiply [a b] (* a b))

(defn ^:after-load on-reload []
  (util/reload-hook-ns 'figwheel-main-testing.g-some)    
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
