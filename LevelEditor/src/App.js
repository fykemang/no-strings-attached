import React, { Component } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Home from "./Home.js";
import { Layout } from "./components/Layout";
import { NavigationBar } from "./components/NavigationBar";

class App extends Component {
  render() {
    return (
      <>
        <Router>
          <NavigationBar />
          <Layout>
            <Switch>
              <Route exact path="/" component={Home} />
            </Switch>
          </Layout>
        </Router>
      </>
    );
  }
}

export default App;
