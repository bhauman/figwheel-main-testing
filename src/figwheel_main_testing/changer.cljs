(ns figwheel-main-testing.changer
  (:require
   [figwheel-main-testing.util :as util]))

(util/add-ns 'figwheel-main-testing.changer)

(defn multiply [a b] (* a b))

;; CHANGES AFTER THIS -----------------------------------------------