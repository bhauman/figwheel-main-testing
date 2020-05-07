(ns figwheel-main-testing.util
  (:require [goog.object :as gobj]))

(defn core-test-fn [nm]
  (if-let [ct js/figwheel_main_testing.test_utils]
    (gobj/get ct nm)
    (fn [& args])))

(defn add-ns [ns]
  ((core-test-fn "add_ns") ns))

(defn reload-hook-ns [ns]
  ((core-test-fn "reload_hook_ns") ns))

(defn signal [data]
  ((core-test-fn "signal") data))

#_(prn (reload-hook-ns 'figwheel-main-testing.ddd))

#_(js/figwheel_main_testing.core_test.add_ns 'figwheel-main-testing.ddd)

(add-ns 'figwheel-main-testing.util)

#_(defn ^:after-load on-reload []
    (when (some? js/figwheel_main_testing.core_test)
      (js/figwheel_main_testing.core_test.add_reload_hook_ns 'figwheel-main-testing.ddd))
    ;; optionally touch your app-state to force rerendering depending on
    ;; your application
    ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
