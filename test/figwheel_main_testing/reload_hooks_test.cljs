(ns  figwheel-main-testing.reload-hooks-test
    (:require
     [cljs.test :refer-macros [deftest is testing async]]
     [goog.string :as gstring]
     [clojure.core.async :refer [<! put! take! go chan close!] :as asyn]
     [figwheel-main-testing.test-utils
      :refer [provide!
              reset-receiver-chan!
              reset-data!
              touch-file!
              touch-files!
              append-line!
              truncate-file!
              drain-chan]
      :as ut]))


;; OK for now we will have to define all the files that
;; we want to be reloaded so that they exist

;; In the real test we will make this file a ^:figwheel-no-load file
;; so that it doesn't get rerun on every change

(provide! "figwheel_main_testing.core")

;; ----------------------------------------------------------------------
;; testing reload hooks
;; ----------------------------------------------------------------------

;; bbb and aa don't have reload hooks metadata

(deftest test-reload-hooks-ddd
  (reset-receiver-chan!)
  (reset-data!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/ddd.cljs"))
     (<! @ut/ns-receiver-chan-atom)
     (<! (asyn/timeout 200))
     ;; there should not be a bbb
     (let [hooks (set (:reload-hook-ns @ut/data-atom))]
       (is (not
            (get hooks
             'figwheel-main-testing.bbb)))
     (is (not
          (get
           hooks
           'figwheel-main-testing.aa)))
     (is (= #{'figwheel-main-testing.core
              'figwheel-main-testing.aaa
              'figwheel-main-testing.ccc
              'figwheel-main-testing.ddd
              'figwheel-main-testing.g-some}
            hooks)))
     (done))))

(deftest test-reload-hooks-ccc
  (reset-receiver-chan!)
  (reset-data!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/ccc.cljs"))
     (<! @ut/ns-receiver-chan-atom)
     (<! (asyn/timeout 200))
     ;; there should not be a bbb
     (let [hooks (set (:reload-hook-ns @ut/data-atom))]
       (is (not
            (get hooks
             'figwheel-main-testing.bbb)))
     (is (not
          (get
           hooks
           'figwheel-main-testing.aa)))
     (is (= #{'figwheel-main-testing.core
              'figwheel-main-testing.aaa
              'figwheel-main-testing.ccc
              'figwheel-main-testing.ddd
              'figwheel-main-testing.g-some}
            hooks)))
     (done))))

(deftest test-reload-hooks-core
  (reset-receiver-chan!)
  (reset-data!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/core.cljs"))
     (<! @ut/ns-receiver-chan-atom)
     (<! (asyn/timeout 200))
     ;; there should not be a bbb
     (let [hooks (set (:reload-hook-ns @ut/data-atom))]
       (is (not
            (get hooks
             'figwheel-main-testing.bbb)))
     (is (not
          (get
           hooks
           'figwheel-main-testing.aa)))
     (is (= #{'figwheel-main-testing.core
              'figwheel-main-testing.aaa
              'figwheel-main-testing.ccc
              'figwheel-main-testing.ddd
              'figwheel-main-testing.g-some}
            hooks)))
     (done))))





;; TODO test exceptions and warnings

;; TODO test javascript reloading

;; TODO turn off dependents loading?

;; TODO need to figure out how to test different settings


#_(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))
