(defproject org.clojars.vladislav/diehard-async "0.1.25"
  :description "FIXME: write description"
  :url "https://github.com/vlnabatov/diehard-async"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/spec.alpha "0.3.218"]
                 [dev.failsafe/failsafe "3.3.0"]]
  :repl-options {:init-ns diehard-async.core})
