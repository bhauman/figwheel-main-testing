(ns ^:figwheel-hooks figwheel-main-testing.core
  (:require
   [figwheel-main-testing.aaa]
   [figwheel-main-testing.aa]
   [figwheel-main-testing.aaaa]
   [figwheel-main-testing.changer]
   [figwheel-main-testing.util :as util]))

(util/add-ns 'figwheel-main-testing.core)

(defn multiply [a b] (* a b))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (util/reload-hook-ns 'figwheel-main-testing.core)    
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
